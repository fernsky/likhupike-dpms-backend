package np.gov.mofaga.imis.auth.api.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import np.gov.mofaga.imis.auth.domain.ElectedPosition
import np.gov.mofaga.imis.auth.domain.OfficePost
import np.gov.mofaga.imis.auth.domain.OfficeSection
import np.gov.mofaga.imis.auth.domain.RoleType
import np.gov.mofaga.imis.auth.domain.UserType
import np.gov.mofaga.imis.shared.validation.annotations.NepaliName
import np.gov.mofaga.imis.shared.validation.annotations.ValidOfficePost
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
    @Schema(example = "1990-01-01", required = false)
    val dateOfBirth: LocalDate?,
    @field:NotBlank(message = "Address cannot be empty")
    @field:Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    @Schema(example = "123 Main St, City", required = true)
    val address: String,
    @field:NotNull(message = "User type is required")
    @Schema(
        example = "LOCAL_LEVEL_EMPLOYEE",
        required = true,
        description = "Type of user (CITIZEN, LOCAL_LEVEL_EMPLOYEE, ELECTED_REPRESENTATIVE, OTHER)"
    )
    val userType: UserType,
    @field:NotBlank(message = "Province code is required")
    @Schema(example = "P1", required = true)
    val provinceCode: String,
    @field:NotBlank(message = "District code is required")
    @Schema(example = "D1", required = true)
    val districtCode: String,
    @field:NotBlank(message = "Municipality code is required")
    @Schema(example = "M1", required = true)
    val municipalityCode: String,
    @Schema(
        example = "1",
        description = "Ward number (required for ward-level employees and ward representatives)",
        minimum = "1",
        maximum = "50"
    )
    @field:Min(value = 1, message = "Ward number must be at least 1")
    @field:Max(value = 50, message = "Ward number cannot exceed 50")
    val wardNumber: Int?,
    @Schema(
        example = "GENERAL_ADMINISTRATION",
        description = "Office section (required for LOCAL_LEVEL_EMPLOYEE)"
    )
    val officeSection: OfficeSection?,
    @Schema(
        example = "WARD_CHAIRPERSON",
        description = "Elected position (required for ELECTED_REPRESENTATIVE)"
    )
    val electedPosition: ElectedPosition?,
    @field:NotBlank(message = "Office post cannot be empty")
    @field:ValidOfficePost
    @Schema(
        example = "Manager",
        description = "Must be one of: Chief Administrative Officer, Manager, Employee, IT Officer, Administrative Officer, Account Officer",
        required = false
    )
    val officePost: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Authentication response payload")
data class AuthResponse(
    @Schema(description = "JWT access token")
    val token: String,
    
    @Schema(description = "JWT refresh token")
    val refreshToken: String?,
    
    @Schema(description = "User identifier")
    val userId: String,
    
    @Schema(description = "User email")
    val email: String,
    
    @Schema(description = "User roles")
    val roles: Set<RoleType>,  // Changed from List to Set
    
    @Schema(description = "Token expiration time in seconds")
    val expiresIn: Long
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "User profile response")
data class UserProfileResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val fullNameNepali: String,
    val dateOfBirth: LocalDate,
    val address: String,
    val profilePicture: String?,
    val userType: UserType,
    val officeSection: OfficeSection?,
    val electedPosition: ElectedPosition?,
    val provinceCode: String,
    val districtCode: String,
    val municipalityCode: String,
    val wardNumber: Int?,
    val officePost: String,
    val roles: Set<RoleType>,
    val isApproved: Boolean,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Request password reset payload")
data class RequestPasswordResetRequest(
    @field:Email(message = "The provided email address is invalid")
    @field:NotBlank(message = "Email address cannot be empty")
    @Schema(example = "user@example.com", required = true)
    val email: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Reset password payload")
data class ResetPasswordRequest(
    @field:NotBlank(message = "Token cannot be empty")
    @Schema(description = "Password reset token received via email", required = true)
    val token: String,

    @field:NotBlank(message = "Password cannot be empty")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$",
        message = """Password must contain:
            - At least 8 characters
            - At least one uppercase letter
            - At least one lowercase letter
            - At least one number
            - At least one special character (@#$%^&+=)"""
    )
    @Schema(example = "NewPassword123#", required = true)
    val newPassword: String
)
