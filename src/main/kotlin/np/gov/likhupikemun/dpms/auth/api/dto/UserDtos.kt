package np.gov.likhupikemun.dpms.auth.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.shared.exception.InvalidInputException
import np.gov.likhupikemun.dpms.shared.validation.annotations.*
import org.springframework.data.domain.Sort
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@Schema(description = "User creation request")
data class CreateUserRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    @Schema(example = "john.doe@example.com", required = true)
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "Password must be at least 8 characters and contain at least one uppercase, lowercase, number and special character",
    )
    @Schema(example = "SecureP@ss123", required = true)
    val password: String,
    @field:NotBlank(message = "Full name is required")
    @Schema(example = "John Doe", required = true)
    val fullName: String,
    @field:NotBlank(message = "Nepali name is required")
    @Schema(example = "जोन डो", required = true)
    val fullNameNepali: String,
    @field:NotNull(message = "Date of birth is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "1990-01-01", required = true)
    val dateOfBirth: String,
    @field:NotBlank(message = "Address is required")
    @Schema(example = "Ward 5, Likhu Pike Municipality", required = true)
    val address: String,
    @field:NotBlank(message = "Office post is required")
    @Schema(example = "WARD_SECRETARY", required = true)
    val officePost: String,
    @field:Min(1) @field:Max(32)
    @Schema(example = "5", minimum = "1", maximum = "32")
    val wardNumber: Int?,
    @Schema(example = "false", defaultValue = "false")
    val isMunicipalityLevel: Boolean = false,
    @Schema(type = "string", format = "binary")
    val profilePicture: MultipartFile?,
    @Schema(example = "[\"EDITOR\", \"VIEWER\"]", defaultValue = "[\"VIEWER\"]")
    val roles: Set<RoleType> = emptySet(),
)

@Schema(description = "User search criteria")
data class UserSearchCriteria(
    @field:Min(1) @field:Max(32)
    @Schema(example = "1", minimum = "1", maximum = "32")
    val wardNumberFrom: Int? = null,
    @field:Min(1) @field:Max(32)
    @Schema(example = "5", minimum = "1", maximum = "32")
    val wardNumberTo: Int? = null,
    @field:Size(min = 2, max = 100)
    @Schema(example = "john", minLength = 2, maxLength = 100)
    val searchTerm: String? = null,
    @Schema(example = "[\"EDITOR\", \"VIEWER\"]")
    val roles: Set<RoleType>? = null,
    @Schema(example = "ACTIVE")
    val status: UserStatus? = null,
    @Schema(example = "[\"WARD_SECRETARY\"]")
    val officePosts: Set<String>? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2024-01-01")
    val dateFrom: LocalDate? = null,
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2024-12-31")
    val dateTo: LocalDate? = null,
    @Schema(example = "false")
    val isMunicipalityLevel: Boolean? = null,
    @Schema(example = "FULL_NAME", defaultValue = "CREATED_AT")
    val sortBy: UserSortField = UserSortField.CREATED_AT,
    @Schema(example = "ASC", defaultValue = "DESC")
    val sortDirection: Sort.Direction = Sort.Direction.DESC,
    @field:Min(0)
    @Schema(example = "0", minimum = "0", defaultValue = "0")
    val page: Int = 0,
    @field:Min(1) @field:Max(100)
    @Schema(example = "20", minimum = "1", maximum = "100", defaultValue = "20")
    val pageSize: Int = 20,
)

@Schema(description = "User account status")
enum class UserStatus {
    @Schema(description = "Awaiting approval")
    PENDING,

    @Schema(description = "Account active")
    ACTIVE,

    @Schema(description = "Account deactivated")
    INACTIVE,
}

@Schema(description = "Available sort fields")
enum class UserSortField {
    @Schema(description = "Sort by creation date")
    CREATED_AT,

    @Schema(description = "Sort by full name")
    FULL_NAME,

    @Schema(description = "Sort by Nepali name")
    FULL_NAME_NEPALI,

    @Schema(description = "Sort by ward number")
    WARD_NUMBER,

    @Schema(description = "Sort by office post")
    OFFICE_POST,

    @Schema(description = "Sort by email")
    EMAIL,

    @Schema(description = "Sort by approval status")
    APPROVAL_STATUS,
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Request to update user details")
data class UpdateUserRequest(
    @field:Schema(
        description = "Updated full name in English",
        example = "John Doe Updated",
    )
    @field:Size(min = 2, max = 100)
    val fullName: String?,
    @field:Schema(
        description = "Updated full name in Nepali",
        example = "जोन डो अपडेटेड",
    )
    @field:NepaliName
    val fullNameNepali: String?,
    @field:Schema(
        description = "Updated address",
        example = "New Address, Ward 5",
    )
    @field:Size(min = 5, max = 200)
    val address: String?,
    @field:Schema(
        description = "Updated office post",
        example = "WARD_SECRETARY",
    )
    @field:ValidOfficePost
    val officePost: String?,
    @field:Schema(
        description = "New profile picture",
        type = "string",
        format = "binary",
    )
    val profilePicture: MultipartFile?,
    @field:Schema(
        description = "Updated user roles",
        example = "[\"EDITOR\", \"VIEWER\"]",
    )
    val roles: Set<RoleType>?,
) {
    fun validate() {
        profilePicture?.let {
            if (it.size > 5 * 1024 * 1024) {
                throw InvalidInputException("Profile picture must not exceed 5MB")
            }
            if (!it.contentType.toString().matches(Regex("image/(jpeg|png)"))) {
                throw InvalidInputException("Profile picture must be JPG or PNG")
            }
        }
    }
}
