package np.gov.likhupikemun.dpms.location.api.dto.criteria

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import np.gov.likhupikemun.dpms.shared.dto.BaseSearchCriteria
import org.springframework.data.domain.Sort
import java.util.*

data class DistrictSearchCriteria(
    @field:Size(max = 100)
    val searchTerm: String? = null,
    @field:Pattern(regexp = "^[A-Z0-9]{1,10}$")
    val code: String? = null,
    val sortBy: DistrictSortField = DistrictSortField.NAME,
    val sortDirection: Sort.Direction = Sort.Direction.ASC,
    page: Int = 0,
    pageSize: Int = 20,
) : BaseSearchCriteria(page, pageSize)

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
