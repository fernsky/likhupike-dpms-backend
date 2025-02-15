package np.gov.likhupikemun.dpms.auth.service.impl

import com.github.benmanes.caffeine.cache.Cache // Change this import
import io.micrometer.observation.annotation.Observed
import np.gov.likhupikemun.dpms.auth.api.dto.AuthResponse
import np.gov.likhupikemun.dpms.auth.api.dto.LoginRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RegisterRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RequestPasswordResetRequest
import np.gov.likhupikemun.dpms.auth.api.dto.ResetPasswordRequest
import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import np.gov.likhupikemun.dpms.auth.exception.*
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.UserRepository
import np.gov.likhupikemun.dpms.shared.event.UserEventPublisher
import np.gov.likhupikemun.dpms.shared.security.jwt.JwtService
import np.gov.likhupikemun.dpms.shared.security.jwt.TokenPair
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import np.gov.likhupikemun.dpms.auth.service.AuthService as AuthService

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val userEventPublisher: UserEventPublisher,
    private val mailSender: JavaMailSender,
    private val resetTokenCache: Cache<String, String>, // Now using Caffeine Cache
) : AuthService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0),
    )
    @Observed(name = "user.registration")
    override fun register(request: RegisterRequest): AuthResponse {
        logger.debug("Processing registration request for email: {}", request.email)
        validateRegistration(request)

        val user = createUser(request)
        val savedUser = userRepository.save(user)
        val tokenPair = jwtService.generateTokenPair(savedUser)

        userEventPublisher.publishUserRegistered(savedUser)
        logger.info("User registered successfully: {}", request.email)

        return createAuthResponse(savedUser, tokenPair)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["userAuth"], key = "#request.email")
    @Observed(name = "user.login")
    override fun login(request: LoginRequest): AuthResponse {
        logger.debug("Processing login request for email: {}", request.email)
        val user = authenticateAndGetUser(request)
        val tokenPair = jwtService.generateTokenPair(user)

        userEventPublisher.publishUserLoggedIn(user)
        logger.info("User logged in successfully: {}", request.email)

        return createAuthResponse(user, tokenPair)
    }

    @Transactional
    override fun refreshToken(refreshToken: String): AuthResponse {
        logger.debug("Processing token refresh request")
        val user = validateRefreshTokenAndGetUser(refreshToken)
        val tokenPair = jwtService.generateTokenPair(user)

        jwtService.invalidateToken(refreshToken)
        logger.info("Token refreshed successfully for user: {}", user.email)

        return createAuthResponse(user, tokenPair)
    }

    @Transactional
    @CacheEvict(value = ["userAuth"], key = "#token")
    override fun logout(token: String) {
        logger.debug("Processing logout request")
        jwtService.invalidateToken(token)
        val email = jwtService.extractEmail(token)
        logger.info("User logged out successfully: {}", email)
    }

    @Transactional
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0),
    )
    override fun requestPasswordReset(request: RequestPasswordResetRequest) {
        val user =
            userRepository.findByEmail(request.email)
                ?: throw UserNotFoundException(request.email)

        val token = generateResetToken()
        resetTokenCache.put(token, user.email)

        sendPasswordResetEmail(user.email, token)
        logger.info("Password reset requested for user: {}", request.email)
    }

    @Transactional
    override fun resetPassword(request: ResetPasswordRequest) {
        val cachedEmail =
            resetTokenCache.getIfPresent(request.token)
                ?: throw InvalidPasswordResetTokenException()

        val user =
            userRepository.findByEmail(cachedEmail)
                ?: throw UserNotFoundException(cachedEmail)

        user.setPassword(passwordEncoder.encode(request.newPassword))
        userRepository.save(user)

        resetTokenCache.invalidate(request.token)
        logger.info("Password reset successful for user: {}", cachedEmail)
    }

    private fun validateRegistration(request: RegisterRequest) {
        if (userRepository.existsByEmail(request.email)) {
            logger.warn("Registration failed: Email already exists: {}", request.email)
            throw EmailAlreadyExistsException(request.email)
        }
    }

    private fun createUser(request: RegisterRequest): User {
        val user = User()
        with(user) {
            email = request.email
            setPassword(passwordEncoder.encode(request.password))
            fullName = request.fullName
            fullNameNepali = request.fullNameNepali
            dateOfBirth = request.dateOfBirth
            address = request.address
            officePost = request.officePost
            wardNumber = request.wardNumber
            isApproved = false
            isMunicipalityLevel = false // Municipality level users should be created by admins
        }
        return user
    }

    private fun authenticateAndGetUser(request: LoginRequest): User {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.email,
                    request.password,
                ),
            )
            return userRepository
                .findByEmail(request.email)
                ?.takeIf { it.isApproved }
                ?: throw UserNotFoundException(request.email)
        } catch (e: Exception) {
            val user = userRepository.findByEmail(request.email)
            when {
                user == null -> throw UserNotFoundException(request.email)
                !user.isApproved -> throw UserNotApprovedException()
                else -> throw InvalidCredentialsException()
            }
        }
    }

    private fun validateRefreshTokenAndGetUser(refreshToken: String): User {
        if (!jwtService.validateToken(refreshToken)) {
            throw InvalidTokenException()
        }

        val email =
            jwtService.extractEmail(refreshToken)
                ?: throw InvalidTokenException()

        return userRepository.findByEmail(email)
            ?: throw UserNotFoundException(email)
    }

    private fun createAuthResponse(
        user: User,
        tokenPair: TokenPair,
    ): AuthResponse =
        AuthResponse(
            token = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
            userId = user.id?.toString() ?: throw IllegalStateException("User ID is null"),
            email = user.email ?: throw IllegalStateException("Email is null"),
            roles = user.roles.mapNotNull { it.roleType }.toSet(),
            expiresIn = tokenPair.expiresIn,
        )

    private fun generateResetToken(): String = UUID.randomUUID().toString()

    private fun sendPasswordResetEmail(
        email: String?,
        token: String,
    ) {
        val userEmail = email ?: throw IllegalArgumentException("Email cannot be null")
        val message =
            SimpleMailMessage().apply {
                setTo(userEmail)
                setSubject("Password Reset Request")
                setText(
                    """
                    You have requested to reset your password.
                    Please use the following token to reset your password: $token
                    
                    If you did not request this, please ignore this email.
                    """.trimIndent(),
                )
            }
        mailSender.send(message)
    }

    // Remove or fix unused method if not needed
    private fun convertToRoleType(role: Role): RoleType = role.roleType ?: throw IllegalStateException("Role type cannot be null")
}
