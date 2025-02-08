package np.gov.likhupikemun.dpms.auth.api.dto

import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.shared.validation.annotations.NepaliName
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import java.time.LocalDate // Add this import

data class LoginRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String
)

data class RegisterRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "Password must contain at least 8 characters, one uppercase, one lowercase, one number and one special character"
    )
    val password: String,

    @field:NotBlank(message = "Full name is required")
    @field:Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    val fullName: String,

    @field:NotBlank(message = "Nepali name is required")
    @field:NepaliName
    val fullNameNepali: String,

    @field:NotNull(message = "Date of birth is required")
    @field:Past(message = "Date of birth must be in the past")
    val dateOfBirth: LocalDate, // Change type to LocalDate

    @field:NotBlank(message = "Address is required")
    val address: String,

    @field:NotBlank(message = "Office post is required")
    val officePost: String,

    @field:Min(value = 1, message = "Ward number must be greater than 0")
    @field:Max(value = 50, message = "Ward number must be less than 50")
    val wardNumber: Int?
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val email: String,
    val roles: List<RoleType>
)
