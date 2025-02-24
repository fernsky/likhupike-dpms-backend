package np.gov.mofaga.imis.location.api.dto.criteria

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.shared.dto.BaseSearchCriteria
import org.springframework.data.domain.Sort

data class ProvinceSearchCriteria(
    @field:Size(max = 100, message = "Search term must not exceed 100 characters")
    val searchTerm: String? = null,
    @field:Pattern(regexp = "^[A-Z0-9]{1,10}$", message = "Code must be 1-10 uppercase letters or numbers")
    val code: String? = null,
    val fields: Set<ProvinceField> = ProvinceField.DEFAULT_FIELDS,
    val includeTotals: Boolean = false,
    val includeGeometry: Boolean = false,
    val includeDistricts: Boolean = false,
    val sortBy: ProvinceSortField = ProvinceSortField.NAME,
    val sortDirection: Sort.Direction = Sort.Direction.ASC,
    override val page: Int = 0,
    override val pageSize: Int = 20,
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
