package np.gov.likhupikemun.dpms.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.auth.api.dto.AuthResponse
import np.gov.likhupikemun.dpms.auth.api.dto.LoginRequest
import np.gov.likhupikemun.dpms.auth.api.dto.RegisterRequest
import np.gov.likhupikemun.dpms.auth.service.AuthService
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
}
