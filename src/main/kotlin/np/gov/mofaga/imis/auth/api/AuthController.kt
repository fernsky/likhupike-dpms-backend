package np.gov.mofaga.imis.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.mofaga.imis.auth.api.dto.AuthResponse
import np.gov.mofaga.imis.auth.api.dto.LoginRequest
import np.gov.mofaga.imis.auth.api.dto.RegisterRequest
import np.gov.mofaga.imis.auth.api.dto.RequestPasswordResetRequest
import np.gov.mofaga.imis.auth.api.dto.ResetPasswordRequest
import np.gov.mofaga.imis.auth.service.AuthService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "Authentication", description = "Authentication management APIs")
@Validated
class AuthController(
    private val authService: AuthService,
) {
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @Operation(summary = "Register a new user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User successfully registered"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "409", description = "User already exists"),
        ],
    )
    @PostMapping("/register", consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody(required = true) request: RegisterRequest,
    ): ResponseEntity<AuthResponse> {
        logger.debug("Processing registration request for email: {}", request.email)
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @Operation(summary = "Authenticate user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "401", description = "Invalid credentials"),
            ApiResponse(responseCode = "403", description = "Account not approved"),
        ],
    )
    @PostMapping("/login", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<AuthResponse> {
        logger.debug("Processing login request for email: {}", request.email)
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Refresh authentication token")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Token successfully refreshed"),
            ApiResponse(responseCode = "401", description = "Invalid refresh token"),
        ],
    )
    @PostMapping("/refresh")
    fun refreshToken(
        @RequestHeader("X-Refresh-Token") refreshToken: String,
    ): ResponseEntity<AuthResponse> {
        logger.debug("Processing token refresh request")
        val response = authService.refreshToken(refreshToken)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Request password reset")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Password reset email sent"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "429", description = "Too many requests"),
        ],
    )
    @PostMapping("/password-reset/request")
    fun requestPasswordReset(
        @Valid @RequestBody request: RequestPasswordResetRequest,
    ): ResponseEntity<Unit> {
        logger.debug("Processing password reset request for email: {}", request.email)
        authService.requestPasswordReset(request)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "Reset password using token")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Password successfully reset"),
            ApiResponse(responseCode = "400", description = "Invalid token or password"),
            ApiResponse(responseCode = "404", description = "User not found"),
        ],
    )
    @PostMapping("/password-reset/reset")
    fun resetPassword(
        @Valid @RequestBody request: ResetPasswordRequest,
    ): ResponseEntity<Unit> {
        logger.debug("Processing password reset with token")
        authService.resetPassword(request)
        return ResponseEntity.ok().build()
    }
}
