package np.gov.likhupikemun.dpms.auth.api.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.shared.exception.InvalidInputException
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Schema(
    description = "Request body for creating a new user",
    requiredProperties = ["email", "password", "fullName", "fullNameNepali", "dateOfBirth", "address", "officePost"],
)
data class CreateUserRequest(
    @field:Schema(
        description = "User's email address (must be unique)",
        example = "john.doe@example.com",
        required = true,
        pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
    )
    @field:Email(message = "Must be a valid email address")
    @field:NotBlank(message = "Email is required")
    val email: String,
    @field:Schema(
        description = "Password (min 8 characters, must contain at least one uppercase, lowercase, number and special character)",
        required = true,
        minLength = 8,
        maxLength = 50,
        example = "SecureP@ss123",
    )
    @field:NotBlank
    @field:Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    val password: String,
    @field:Schema(
        description = "Full name in English",
        example = "John Doe",
        required = true,
        minLength = 2,
        maxLength = 100,
    )
    @field:NotBlank
    @field:Size(min = 2, max = 100)
    val fullName: String,
    @field:Schema(
        description = "Full name in Nepali",
        example = "जोन डो",
        required = true,
        minLength = 2,
        maxLength = 100,
    )
    @field:NotBlank
    @field:Size(min = 2, max = 100)
    val fullNameNepali: String,
    @field:Schema(
        description = "Date of birth in ISO format (YYYY-MM-DD)",
        example = "1990-01-01",
        required = true,
        pattern = "^\\d{4}-\\d{2}-\\d{2}$",
    )
    @field:NotBlank
    val dateOfBirth: String,
    @field:Schema(
        description = "Residential or official address",
        example = "Ward 5, Likhu Pike Municipality, Nepal",
        required = true,
        minLength = 5,
        maxLength = 200,
    )
    @field:NotBlank
    @field:Size(min = 5, max = 200)
    val address: String?,
    @field:Schema(
        description = "Official post in municipality/ward",
        example = "WARD_SECRETARY",
        allowableValues = ["MUNICIPALITY_ADMIN", "WARD_CHAIRMAN", "WARD_SECRETARY", "TECHNICAL_ASSISTANT"],
        required = true,
    )
    @field:NotBlank
    val officePost: String?,
    @field:Schema(
        description = "Ward number (1-32). Null for municipality level users",
        example = "5",
        minimum = "1",
        maximum = "32",
        nullable = true,
    )
    @field:Min(1)
    @field:Max(32)
    val wardNumber: Int?,
    @field:Schema(
        description = "Indicates if user operates at municipality level",
        example = "false",
        defaultValue = "false",
    )
    val isMunicipalityLevel: Boolean = false,
    @field:Schema(
        description = "Profile picture file (max 5MB, allowed types: jpg, png)",
        type = "string",
        format = "binary",
        nullable = true,
    )
    val profilePicture: MultipartFile? = null,
    @field:Schema(
        example = "[\"EDITOR\", \"VIEWER\"]",
        defaultValue = "[\"VIEWER\"]",
        allowableValues = ["MUNICIPALITY_ADMIN", "WARD_ADMIN", "EDITOR", "VIEWER"],
    )
    val roles: Set<RoleType> = emptySet(),
) {
    // You might want to add custom validation here
    fun validate() {
        // Validate date format
        try {
            LocalDate.parse(dateOfBirth)
        } catch (e: DateTimeParseException) {
            throw InvalidInputException("Invalid date format for dateOfBirth. Expected format: YYYY-MM-DD")
        }

        // Validate ward number and municipality level combination
        if (isMunicipalityLevel && wardNumber != null) {
            throw InvalidInputException("Municipality level users cannot be assigned to a specific ward")
        }

        // Validate profile picture if present
        profilePicture?.let { file ->
            if (file.size > 5 * 1024 * 1024) { // 5MB limit
                throw InvalidInputException("Profile picture size must not exceed 5MB")
            }
            if (!file.contentType.toString().matches(Regex("image/(jpeg|png)"))) {
                throw InvalidInputException("Profile picture must be either JPG or PNG format")
            }
        }
    }
}
