package np.gov.likhupikemun.dpms.auth.service.impl

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import np.gov.likhupikemun.dpms.auth.api.dto.LoginRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RegisterRequest
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
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
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

    @InjectMockKs
    private lateinit var authService: AuthServiceImpl

    private val mockUser =
        User(
            id = "1", // Changed to String
            email = "test@example.com",
            password = "encoded_password",
            fullName = "Test User",
            fullNameNepali = "टेस्ट युजर",
            dateOfBirth = LocalDate.now(),
            address = "Test Address",
            officePost = "Test Post",
            wardNumber = 1,
            isApproved = true,
            roles = mutableSetOf(Role("1", RoleType.VIEWER)), // Fixed Role constructor call
        )

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
            User(
                id = "1",
                email = "test@example.com",
                password = "encoded_password",
                fullName = "Test User",
                fullNameNepali = "टेस्ट युजर",
                dateOfBirth = LocalDate.now(),
                address = "Test Address",
                officePost = "Test Post",
                wardNumber = 1,
                isApproved = false, // This is the key difference
                roles = mutableSetOf(Role("1", RoleType.VIEWER)),
            )

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
}
