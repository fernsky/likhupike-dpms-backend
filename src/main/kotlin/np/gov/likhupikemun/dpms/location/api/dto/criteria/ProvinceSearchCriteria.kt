package np.gov.likhupikemun.dpms.location.api.dto.criteria

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import np.gov.likhupikemun.dpms.shared.dto.BaseSearchCriteria
import org.springframework.data.domain.Sort

data class ProvinceSearchCriteria(
    @field:Size(max = 100, message = "Search term must not exceed 100 characters")
    val searchTerm: String? = null,
    @field:Pattern(regexp = "^[A-Z0-9]{1,10}$", message = "Code must be 1-10 uppercase letters or numbers")
    val code: String? = null,
    val sortBy: ProvinceSortField = ProvinceSortField.NAME,
    val sortDirection: Sort.Direction = Sort.Direction.ASC,
    page: Int = 0,
    pageSize: Int = 20,
) : BaseSearchCriteria(page, pageSize)

enum class ProvinceSortField {
    NAME,
    CODE,
    POPULATION,
    AREA,
    DISTRICT_COUNT,
    CREATED_AT,
    ;

    fun toEntityField(): String =
        when (this) {
            NAME -> "name"
            CODE -> "code"
            POPULATION -> "population"
            AREA -> "area"
            DISTRICT_COUNT -> "districts.size"
            CREATED_AT -> "createdAt"
        }
}
