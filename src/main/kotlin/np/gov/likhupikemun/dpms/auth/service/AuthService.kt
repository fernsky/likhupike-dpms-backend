package np.gov.likhupikemun.dpms.auth.service

import np.gov.likhupikemun.dpms.auth.api.dto.AuthResponse
import np.gov.likhupikemun.dpms.auth.api.dto.LoginRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RegisterRequest
import np.gov.likhupikemun.dpms.auth.domain.User
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.UserRepository
import np.gov.likhupikemun.dpms.shared.config.JwtService
import np.gov.likhupikemun.dpms.shared.exception.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.retry.annotation.Retryable
import org.springframework.retry.annotation.Backoff
import java.time.LocalDate
import io.micrometer.observation.annotation.Observed
import org.springframework.cache.annotation.Cacheable

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,  // Ensure PasswordEncoder is injected
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
) {
    @Transactional
    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    @Observed(name = "user.registration")
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException(request.email)
        }

        val dateOfBirth: LocalDate = request.dateOfBirth // Directly use request.dateOfBirth

        val user =
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),  // Changed from password to _password
                fullName = request.fullName,
                fullNameNepali = request.fullNameNepali,
                dateOfBirth = dateOfBirth,
                address = request.address,
                officePost = request.officePost,
                wardNumber = request.wardNumber,
                isApproved = false,
            )

        val savedUser = userRepository.save(user)
        val token = jwtService.generateToken(user)

        return createAuthResponse(savedUser, token)
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["userAuth"], key = "#request.email")
    @Observed(name = "user.login")
    fun login(request: LoginRequest): AuthResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
        } catch (e: Exception) {
            throw InvalidCredentialsException()
        }

        val user =
            userRepository.findByEmail(request.email)
                ?: throw UserNotFoundException(request.email)

        if (!user.isApproved) {
            throw UserNotApprovedException(request.email)
        }

        val token = jwtService.generateToken(user)

        return createAuthResponse(user, token)
    }

    private fun createAuthResponse(user: User, token: String) = AuthResponse(
        token = token,
        userId = user.id!!,
        email = user.email,
        roles = user.roles.map { it.name }
    )
}
