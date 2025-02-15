package np.gov.likhupikemun.dpms.auth.service.impl

import com.github.benmanes.caffeine.cache.Cache
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import np.gov.likhupikemun.dpms.auth.api.dto.*
import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import np.gov.likhupikemun.dpms.auth.exception.*
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.UserRepository
import np.gov.likhupikemun.dpms.shared.event.UserEventPublisher
import np.gov.likhupikemun.dpms.shared.security.jwt.JwtService
import np.gov.likhupikemun.dpms.shared.security.jwt.TokenPair
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthServiceImplTest {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var jwtService: JwtService

    @MockK
    private lateinit var authenticationManager: AuthenticationManager

    @MockK
    private lateinit var userEventPublisher: UserEventPublisher

    @MockK
    private lateinit var mailSender: JavaMailSender

    @MockK
    private lateinit var resetTokenCache: Cache<String, String>

    @InjectMockKs
    private lateinit var authService: AuthServiceImpl

    private val mockRole =
        Role().apply {
            id = UUID.randomUUID()
            roleType = RoleType.VIEWER
        }

    private val mockUser =
        User().apply {
            id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
            email = "test@example.com"
            setPassword("encoded_password")
            fullName = "Test User"
            fullNameNepali = "टेस्ट युजर"
            dateOfBirth = LocalDate.now()
            address = "Test Address"
            officePost = "Test Post"
            wardNumber = 1
            isApproved = true
            roles = mutableSetOf(mockRole)
        }

    private val mockTokenPair =
        TokenPair(
            accessToken = "access_token",
            refreshToken = "refresh_token",
            expiresIn = 3600L,
        )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `register should create new user and return auth response`() {
        // Arrange
        val request =
            RegisterRequest(
                email = "test@example.com",
                password = "password123",
                fullName = "Test User",
                fullNameNepali = "टेस्ट युजर",
                dateOfBirth = LocalDate.now(),
                address = "Test Address",
                officePost = "Test Post",
                wardNumber = 1,
            )

        every { userRepository.existsByEmail(any()) } returns false
        every { passwordEncoder.encode(any()) } returns "encoded_password"
        every { userRepository.save(any()) } returns mockUser
        every { jwtService.generateTokenPair(any()) } returns mockTokenPair
        justRun { userEventPublisher.publishUserRegistered(any()) }

        // Act
        val result = authService.register(request)

        // Assert
        assertNotNull(result)
        assertEquals(mockUser.email, result.email)
        verify { userRepository.save(any()) }
        verify { userEventPublisher.publishUserRegistered(any()) }
    }

    @Test
    fun `register should throw EmailAlreadyExistsException when email exists`() {
        val request =
            RegisterRequest(
                email = "test@example.com",
                password = "password123",
                fullName = "Test User",
                fullNameNepali = "टेस्ट युजर",
                dateOfBirth = LocalDate.now(),
                address = "Test Address",
                officePost = "Test Post",
                wardNumber = 1,
            )

        every { userRepository.existsByEmail(any()) } returns true

        assertThrows<EmailAlreadyExistsException> {
            authService.register(request)
        }
    }

    @Test
    fun `login should authenticate user and return auth response`() {
        // Arrange
        val request = LoginRequest("test@example.com", "password123")
        val authToken = UsernamePasswordAuthenticationToken(request.email, request.password)

        every { authenticationManager.authenticate(any()) } returns authToken
        every { userRepository.findByEmail(request.email) } returns mockUser
        every { jwtService.generateTokenPair(any()) } returns mockTokenPair
        justRun { userEventPublisher.publishUserLoggedIn(any()) }

        // Act
        val result = authService.login(request)

        // Assert
        assertNotNull(result)
        assertEquals(mockUser.email, result.email)
        verify {
            authenticationManager.authenticate(
                match {
                    it.principal == request.email && it.credentials == request.password
                },
            )
        }
        verify { userEventPublisher.publishUserLoggedIn(mockUser) }
    }

    @Test
    fun `login should throw InvalidCredentialsException when authentication fails`() {
        val request = LoginRequest("test@example.com", "wrong_password")

        every { authenticationManager.authenticate(any()) } throws RuntimeException()
        every { userRepository.findByEmail(request.email) } returns mockUser

        assertThrows<InvalidCredentialsException> {
            authService.login(request)
        }
    }

    @Test
    fun `login should throw UserNotFoundException when user not found`() {
        // Arrange
        val request = LoginRequest("test@example.com", "password123")
        val authToken = UsernamePasswordAuthenticationToken(request.email, request.password)

        every { authenticationManager.authenticate(any()) } returns authToken
        every { userRepository.findByEmail(request.email) } returns null

        // Act & Assert
        assertThrows<UserNotFoundException> {
            authService.login(request)
        }
    }

    @Test
    fun `login should throw UserNotApprovedException when user is not approved`() {
        // Arrange
        val request = LoginRequest("test@example.com", "password123")
        val authToken = UsernamePasswordAuthenticationToken(request.email, request.password)
        val unapprovedUser =
            User().apply {
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001")
                email = "test@example.com"
                setPassword("encoded_password")
                fullName = "Test User"
                fullNameNepali = "टेस्ट युजर"
                dateOfBirth = LocalDate.now()
                address = "Test Address"
                officePost = "Test Post"
                wardNumber = 1
                isApproved = false
                roles = mutableSetOf(mockRole)
            }

        every { authenticationManager.authenticate(any()) } returns authToken
        every { userRepository.findByEmail(request.email) } returns unapprovedUser

        // Act & Assert
        assertThrows<UserNotApprovedException> {
            authService.login(request)
        }
    }

    @Test
    fun `refreshToken should return new token pair`() {
        // Arrange
        val refreshToken = "valid_refresh_token"

        every { jwtService.validateToken(any()) } returns true
        every { jwtService.extractEmail(any()) } returns "test@example.com"
        every { userRepository.findByEmail(any()) } returns mockUser
        every { jwtService.generateTokenPair(any()) } returns mockTokenPair
        justRun { jwtService.invalidateToken(any()) }

        // Act
        val result = authService.refreshToken(refreshToken)

        // Assert
        assertNotNull(result)
        assertEquals(mockTokenPair.accessToken, result.token)
        verify { jwtService.invalidateToken(refreshToken) }
    }

    @Test
    fun `refreshToken should throw InvalidTokenException for invalid token`() {
        val refreshToken = "invalid_refresh_token"

        every { jwtService.validateToken(any()) } returns false

        assertThrows<InvalidTokenException> {
            authService.refreshToken(refreshToken)
        }
    }

    @Test
    fun `logout should invalidate token`() {
        // Arrange
        val token = "valid_token"

        justRun { jwtService.invalidateToken(any()) }
        every { jwtService.extractEmail(any()) } returns "test@example.com"

        // Act
        authService.logout(token)

        // Verify
        verify { jwtService.invalidateToken(token) }
    }

    @Test
    fun `requestPasswordReset - should send reset email when user exists`() {
        // Arrange
        val email = "test@example.com"
        val request = RequestPasswordResetRequest(email)

        every { userRepository.findByEmail(email) } returns mockUser
        every { resetTokenCache.put(any(), email) } returns Unit
        every { mailSender.send(any<SimpleMailMessage>()) } just Runs

        // Act
        authService.requestPasswordReset(request)

        // Verify
        verify {
            mailSender.send(any<SimpleMailMessage>())
            resetTokenCache.put(any(), email)
        }
    }

    @Test
    fun `requestPasswordReset - should throw when user not found`() {
        val email = "nonexistent@example.com"
        val request = RequestPasswordResetRequest(email)

        every { userRepository.findByEmail(email) } returns null

        assertThrows<UserNotFoundException> {
            authService.requestPasswordReset(request)
        }
    }

    @Test
    fun `resetPassword - should update password when token is valid`() {
        // Arrange
        val email = "test@example.com"
        val token = "valid-token"
        val newPassword = "NewPassword123#"
        val request = ResetPasswordRequest(token, newPassword)

        every { resetTokenCache.getIfPresent(token) } returns email
        every { userRepository.findByEmail(email) } returns mockUser
        every { passwordEncoder.encode(newPassword) } returns "encoded-password"
        every { userRepository.save(any()) } returns mockUser
        every { resetTokenCache.invalidate(token) } returns Unit

        // Act
        authService.resetPassword(request)

        // Verify
        verify {
            userRepository.save(any())
            resetTokenCache.invalidate(token)
        }
    }

    @Test
    fun `resetPassword - should throw when token is invalid`() {
        val request = ResetPasswordRequest("invalid-token", "NewPassword123#")
        every { resetTokenCache.getIfPresent(any()) } returns null

        assertThrows<InvalidPasswordResetTokenException> {
            authService.resetPassword(request)
        }
    }
}
