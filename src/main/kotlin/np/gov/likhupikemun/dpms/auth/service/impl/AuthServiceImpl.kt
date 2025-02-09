package np.gov.likhupikemun.dpms.auth.service.impl

import io.micrometer.observation.annotation.Observed
import np.gov.likhupikemun.dpms.auth.api.dto.AuthResponse
import np.gov.likhupikemun.dpms.auth.api.dto.LoginRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RegisterRequest
import np.gov.likhupikemun.dpms.auth.domain.User
import np.gov.likhupikemun.dpms.auth.exception.*
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.UserRepository
import np.gov.likhupikemun.dpms.shared.event.UserEventPublisher
import np.gov.likhupikemun.dpms.shared.security.jwt.JwtService
import np.gov.likhupikemun.dpms.shared.security.jwt.TokenPair
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import np.gov.likhupikemun.dpms.auth.service.AuthService as AuthService

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val userEventPublisher: UserEventPublisher,
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

    private fun validateRegistration(request: RegisterRequest) {
        if (userRepository.existsByEmail(request.email)) {
            logger.warn("Registration failed: Email already exists: {}", request.email)
            throw EmailAlreadyExistsException(request.email)
        }
    }

    private fun createUser(request: RegisterRequest) =
        User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            fullNameNepali = request.fullNameNepali,
            dateOfBirth = request.dateOfBirth,
            address = request.address,
            officePost = request.officePost,
            wardNumber = request.wardNumber,
            isApproved = false,
        )

    private fun authenticateAndGetUser(request: LoginRequest): User {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password),
            )
        } catch (e: Exception) {
            logger.warn("Authentication failed for user: {}", request.email)
            throw InvalidCredentialsException()
        }

        return userRepository
            .findByEmail(request.email)
            ?.takeIf { it.isApproved }
            ?: run {
                logger.warn("User not found or not approved: {}", request.email)
                throw UserNotFoundException(request.email)
            }
    }

    private fun validateRefreshTokenAndGetUser(refreshToken: String): User {
        if (!jwtService.validateToken(refreshToken)) {
            logger.warn("Invalid refresh token")
            throw InvalidTokenException()
        }

        val email = jwtService.extractEmail(refreshToken)
        return userRepository.findByEmail(email) ?: run {
            logger.warn("User not found for refresh token: {}", email)
            throw UserNotFoundException(email)
        }
    }

    private fun createAuthResponse(
        user: User,
        tokenPair: TokenPair,
    ) = AuthResponse(
        token = tokenPair.accessToken,
        refreshToken = tokenPair.refreshToken,
        userId = user.id!!,
        email = user.email,
        roles = user.roles.map { it.name },
        expiresIn = tokenPair.expiresIn,
    )
}
