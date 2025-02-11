package np.gov.likhupikemun.dpms.auth.api.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.auth.api.dto.UserStatus
import np.gov.likhupikemun.dpms.auth.domain.RoleType // Updated import to use domain RoleType
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
    @Schema(example = "[\"EDITOR\", \"VIEWER\"]")
    val roles: Set<RoleType>? = null, // Now using domain RoleType
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
    @field:Schema(
        description = "Field to sort by",
        example = "FULL_NAME",
        defaultValue = "CREATED_AT",
    )
    @field:NotNull
    val sortBy: UserSortField = UserSortField.CREATED_AT,
    @field:Schema(
        description = "Sort direction",
        example = "ASC",
        defaultValue = "DESC",
    )
    @field:NotNull
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

    override fun toString(): String =
        "UserSearchCriteria(page=$page, pageSize=$pageSize, sortBy=$sortBy, " +
            "sortDirection=$sortDirection, searchTerm=$searchTerm, " +
            "wardNumberFrom=$wardNumberFrom, wardNumberTo=$wardNumberTo, " +
            "roles=$roles, officePosts=$officePosts, isApproved=$isApproved, " +
            "isMunicipalityLevel=$isMunicipalityLevel, " +
            "createdAfter=$createdAfter, createdBefore=$createdBefore, " +
            "dateOfBirthFrom=$dateOfBirthFrom, dateOfBirthTo=$dateOfBirthTo)"
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
