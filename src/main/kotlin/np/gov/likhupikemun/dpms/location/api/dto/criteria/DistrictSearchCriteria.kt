package np.gov.likhupikemun.dpms.location.api.dto.criteria

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import np.gov.likhupikemun.dpms.shared.dto.BaseSearchCriteria
import np.gov.likhupikemun.dpms.shared.exception.ValidationException
import org.springframework.data.domain.Sort
import java.util.*

data class DistrictSearchCriteria(
    @field:Size(max = 100)
    val searchTerm: String? = null,
    @field:Pattern(regexp = "^[A-Z0-9]{1,10}$")
    val code: String? = null,
    val sortBy: DistrictSortField = DistrictSortField.NAME,
    val sortDirection: Sort.Direction = Sort.Direction.ASC,
    override val page: Int = 0,
    override val pageSize: Int = 20,
) : BaseSearchCriteria(page, pageSize) {
    fun validate() {
        val errors = mutableMapOf<String, String>()

        if (page < 0) errors["page"] = "Page number cannot be negative"
        if (pageSize < 1) errors["pageSize"] = "Page size must be positive"
        if (pageSize > 100) errors["pageSize"] = "Page size cannot exceed 100"

        code?.let {
            if (!it.matches(Regex("^[A-Z0-9]{1,10}$"))) {
                errors["code"] = "Invalid district code format"
            }
        }

        searchTerm?.let {
            if (it.length > 100) {
                errors["searchTerm"] = "Search term cannot exceed 100 characters"
            }
        }

        if (errors.isNotEmpty()) {
            throw ValidationException("Invalid search criteria", errors)
        }
    }
}

enum class DistrictSortField {
    NAME,
    CODE,
    POPULATION,
    AREA,
    MUNICIPALITY_COUNT,
    CREATED_AT,
    ;

    fun toEntityField(): String =
        when (this) {
            NAME -> "name"
            CODE -> "code"
            POPULATION -> "population"
            AREA -> "area"
            MUNICIPALITY_COUNT -> "municipalities.size"
            CREATED_AT -> "createdAt"
        }
}
