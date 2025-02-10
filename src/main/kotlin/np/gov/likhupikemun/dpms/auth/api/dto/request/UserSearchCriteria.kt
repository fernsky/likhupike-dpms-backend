package np.gov.likhupikemun.dpms.auth.api.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.auth.api.dto.RoleType
import np.gov.likhupikemun.dpms.auth.api.dto.UserStatus
import np.gov.likhupikemun.dpms.auth.exception.InvalidSearchCriteriaException
import org.springframework.data.domain.Sort
import java.time.LocalDate

@Schema(description = "Search criteria for filtering users")
data class UserSearchCriteria(
    @field:Schema(
        description = "Minimum ward number",
        example = "1",
        minimum = "1",
        maximum = "32",
    )
    @field:Min(1)
    @field:Max(32)
    val wardNumberFrom: Int? = null,
    @field:Schema(
        description = "Maximum ward number",
        example = "5",
        minimum = "1",
        maximum = "32",
    )
    @field:Min(1)
    @field:Max(32)
    val wardNumberTo: Int? = null,
    @field:Schema(
        description = "Search term for name or email",
        example = "john",
        minLength = 2,
        maxLength = 100,
    )
    @field:Size(min = 2, max = 100)
    val searchTerm: String? = null,
    val roles: Set<RoleType>? = null,
    val status: UserStatus? = null,
    val officePosts: Set<String>? = null,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val createdAfter: LocalDate? = null,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val createdBefore: LocalDate? = null,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val dateOfBirthFrom: LocalDate? = null,
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val dateOfBirthTo: LocalDate? = null,
    val isApproved: Boolean? = null,
    val isMunicipalityLevel: Boolean? = null,
    @field:Pattern(regexp = "^[a-zA-Z_]+$")
    @field:Schema(
        description = "Field to sort by",
        example = "FULL_NAME",
        defaultValue = "CREATED_AT",
    )
    val sortBy: UserSortField = UserSortField.CREATED_AT,
    @field:Schema(
        description = "Sort direction",
        example = "ASC",
        defaultValue = "DESC",
    )
    val sortDirection: Sort.Direction = Sort.Direction.DESC,
    @field:Min(1) @field:Max(100)
    val pageSize: Int = 20,
    @field:Min(0)
    val page: Int = 0,
) {
    fun validate() {
        if (wardNumberFrom != null && wardNumberTo != null && wardNumberFrom > wardNumberTo) {
            throw InvalidSearchCriteriaException("wardNumberFrom cannot be greater than wardNumberTo")
        }
        if (createdAfter != null && createdBefore != null && createdAfter.isAfter(createdBefore)) {
            throw InvalidSearchCriteriaException("createdAfter cannot be after createdBefore")
        }
        if (dateOfBirthFrom != null && dateOfBirthTo != null && dateOfBirthFrom.isAfter(dateOfBirthTo)) {
            throw InvalidSearchCriteriaException("dateOfBirthFrom cannot be after dateOfBirthTo")
        }
    }
}

@Schema(description = "Sort field options for user search")
enum class UserSortField {
    @Schema(description = "Sort by creation date")
    CREATED_AT,

    @Schema(description = "Sort by English name")
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
