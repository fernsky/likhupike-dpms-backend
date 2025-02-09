package np.gov.likhupikemun.dpms.auth.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.auth.domain.OfficePost
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.shared.validation.annotations.NepaliName
import np.gov.likhupikemun.dpms.shared.validation.annotations.ValidOfficePost
import np.gov.likhupikemun.dpms.shared.validation.annotations.ValidOfficePostWardCombination
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Login request payload")
data class LoginRequest(
    @field:Email(message = "The provided email address is invalid")
    @field:NotBlank(message = "Email address cannot be empty")
    @Schema(example = "user@example.com", required = true)
    val email: String,
    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(example = "Password123#", required = true)
    val password: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Registration request payload")
@ValidOfficePostWardCombination
data class RegisterRequest(
    @field:Email(message = "The provided email address is invalid")
    @field:NotBlank(message = "Email address cannot be empty")
    @Schema(example = "user@example.com", required = true)
    val email: String,
    @field:NotBlank(message = "Password cannot be empty")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$",
        message = """Password must contain:
            - At least 8 characters
            - At least one uppercase letter
            - At least one lowercase letter
            - At least one number
            - At least one special character (@#$%^&+=)""",
    )
    @Schema(
        example = "Password123#",
        required = true,
        description = "Password must be at least 8 characters long and contain uppercase, lowercase, number and special character",
    )
    val password: String,
    @field:NotBlank(message = "Full name cannot be empty")
    @field:Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Schema(example = "John Doe", required = true)
    val fullName: String,
    @field:NotBlank(message = "Nepali name cannot be empty")
    @field:NepaliName(message = "Please enter a valid name in Nepali")
    @Schema(example = "जोन डो", required = true)
    val fullNameNepali: String,
    @field:NotNull(message = "Date of birth is required")
    @field:Past(message = "Date of birth must be a past date")
    @Schema(example = "1990-01-01", required = true)
    val dateOfBirth: LocalDate,
    @field:NotBlank(message = "Address cannot be empty")
    @field:Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    @Schema(example = "123 Main St, City", required = true)
    val address: String,
    @field:NotBlank(message = "Office post cannot be empty")
    @field:ValidOfficePost
    @Schema(
        example = "Manager",
        description = "Must be one of: Chief Administrative Officer, Manager, Employee, IT Officer, Administrative Officer, Account Officer",
        required = true
    )
    val officePost: String,
    @Schema(
        example = "1",
        description = "Ward number (not applicable for Chief Administrative Officer)",
        minimum = "1",
        maximum = "50"
    )
    @field:Min(value = 1, message = "Ward number must be at least 1")
    @field:Max(value = 50, message = "Ward number cannot exceed 50")
    val wardNumber: Int?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Authentication response payload")
data class AuthResponse(
    @Schema(description = "JWT access token")
    val token: String,
    @Schema(description = "User identifier")
    val userId: String,
    @Schema(description = "User email")
    val email: String,
    @Schema(description = "User roles")
    val roles: List<RoleType>,
    @Schema(description = "Token expiration time in seconds")
    val expiresIn: Long,
    @Schema(description = "Refresh token")
    val refreshToken: String?,
)
