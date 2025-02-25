package np.gov.mofaga.imis.location.api.dto.criteria

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import np.gov.mofaga.imis.location.api.dto.enums.DistrictField
import np.gov.mofaga.imis.shared.dto.BaseSearchCriteria
import np.gov.mofaga.imis.shared.exception.ValidationException
import org.springframework.data.domain.Sort
import java.util.*

data class DistrictSearchCriteria(
    @field:Size(max = 100, message = "Search term must not exceed 100 characters")
    val searchTerm: String? = null,
    @field:Pattern(regexp = "^[A-Z0-9]{1,10}$", message = "Code must be 1-10 uppercase letters or numbers")
    val code: String? = null,
    val provinceCode: String? = null,
    val fields: Set<DistrictField> = DistrictField.DEFAULT_FIELDS,
    val includeTotals: Boolean = false,
    val includeGeometry: Boolean = false,
    val includeMunicipalities: Boolean = false,
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
