package np.gov.likhupikemun.dpms.auth.api

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.auth.api.dto.*
import np.gov.likhupikemun.dpms.auth.domain.OfficePost
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.exception.*
import np.gov.likhupikemun.dpms.auth.service.AuthService
import np.gov.likhupikemun.dpms.config.TestSecurityConfig
import np.gov.likhupikemun.dpms.shared.security.jwt.JwtService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.*

@WebMvcTest(AuthController::class)
@Import(TestSecurityConfig::class)
@AutoConfigureTestDatabase
class AuthControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var jwtService: JwtService

    private val testAuthResponse =
        AuthResponse(
            userId = UUID.randomUUID().toString(),
            email = "test@example.com",
            token = "test-token",
            refreshToken = "test-refresh-token",
            expiresIn = 3600,
            roles = listOf(RoleType.VIEWER),
        )

    private val baseRegisterRequest =
        RegisterRequest(
            email = "test@example.com",
            password = "Password@123",
            fullName = "Test User",
            fullNameNepali = "टेस्ट युजर",
            officePost = OfficePost.CHIEF_ADMINISTRATIVE_OFFICER.title,
            wardNumber = null,
            dateOfBirth = LocalDate.of(1990, 1, 1),
            address = "Test Address",
        )

    @Test
    fun `register - should return 201 when registration is successful`() {
        whenever(authService.register(any())).thenReturn(testAuthResponse)

        mockMvc
            .perform(
                post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(baseRegisterRequest)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.token").value(testAuthResponse.token))
            .andExpect(jsonPath("$.refreshToken").value(testAuthResponse.refreshToken))
            .andExpect(jsonPath("$.expiresIn").value(testAuthResponse.expiresIn))
    }

    @Test
    fun `register - should return 409 when email already exists`() {
        whenever(authService.register(any()))
            .thenThrow(EmailAlreadyExistsException(baseRegisterRequest.email))

        mockMvc
            .perform(
                post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(baseRegisterRequest)),
            ).andExpect(status().isConflict)
    }

    @Test
    fun `login - should return 200 when login is successful`() {
        val loginRequest =
            LoginRequest(
                email = "test@example.com",
                password = "password123",
            )
        whenever(authService.login(any())).thenReturn(testAuthResponse)

        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value(testAuthResponse.token))
            .andExpect(jsonPath("$.refreshToken").value(testAuthResponse.refreshToken))
    }

    @Test
    fun `login - should return 401 when credentials are invalid`() {
        val loginRequest =
            LoginRequest(
                email = "test@example.com",
                password = "wrongpassword",
            )
        whenever(authService.login(any())).thenThrow(InvalidCredentialsException())

        mockMvc
            .perform(
                post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `refreshToken - should return 200 when token refresh is successful`() {
        whenever(authService.refreshToken(any())).thenReturn(testAuthResponse)

        mockMvc
            .perform(
                post("/api/v1/auth/refresh")
                    .header("X-Refresh-Token", "valid-refresh-token"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.token").value(testAuthResponse.token))
    }

    @Test
    fun `refreshToken - should return 401 when refresh token is expired`() {
        whenever(authService.refreshToken(any())).thenThrow(TokenExpiredException())

        mockMvc
            .perform(
                post("/api/v1/auth/refresh")
                    .header("X-Refresh-Token", "expired-token"),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `requestPasswordReset - should return 200 when request is valid`() {
        val request = RequestPasswordResetRequest("test@example.com")
        doNothing().whenever(authService).requestPasswordReset(any())

        mockMvc
            .perform(
                post("/api/v1/auth/password-reset/request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
    }

    @Test
    fun `resetPassword - should return 200 when reset is successful`() {
        val request = ResetPasswordRequest("valid-token", "NewPassword123#")
        doNothing().whenever(authService).resetPassword(any())

        mockMvc
            .perform(
                post("/api/v1/auth/password-reset/reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
    }

    @Test
    fun `resetPassword - should return 400 when token is invalid`() {
        val request = ResetPasswordRequest("invalid-token", "NewPassword123#")
        doThrow(InvalidPasswordResetTokenException())
            .whenever(authService)
            .resetPassword(any())

        mockMvc
            .perform(
                post("/api/v1/auth/password-reset/reset")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isBadRequest)
    }
}
