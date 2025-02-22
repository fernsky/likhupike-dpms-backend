package np.gov.mofaga.imis.location.api.dto.criteria

import jakarta.validation.constraints.*
import np.gov.mofaga.imis.location.api.dto.enums.MunicipalitySortField
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.shared.dto.BaseSearchCriteria
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.util.*

data class MunicipalitySearchCriteria(
    @field:Size(max = 100)
    val searchTerm: String? = null,
    @field:Pattern(regexp = "^[A-Z0-9]{1,10}$")
    val code: String? = null,
    val districtCode: String? = null,
    val provinceCode: String? = null,
    val types: Set<MunicipalityType>? = null,
    @field:Min(1) @field:Max(33)
    val minWards: Int? = null,
    @field:Min(1) @field:Max(33)
    val maxWards: Int? = null,
    @field:Positive
    val minPopulation: Long? = null,
    @field:Positive
    val maxPopulation: Long? = null,
    @field:Positive
    val minArea: BigDecimal? = null,
    @field:Positive
    val maxArea: BigDecimal? = null,
    @field:DecimalMin("-90") @field:DecimalMax("90")
    val latitude: BigDecimal? = null,
    @field:DecimalMin("-180") @field:DecimalMax("180")
    val longitude: BigDecimal? = null,
    @field:Positive
    val radiusKm: Double? = null,
    val sortBy: MunicipalitySortField = MunicipalitySortField.NAME,
    val sortDirection: Sort.Direction = Sort.Direction.ASC,
    @field:Min(0)
    override val page: Int = 0,
    @field:Min(1)
    override val pageSize: Int = 20,
) : BaseSearchCriteria(page, pageSize) {
    fun validate() {
        require(!(minWards != null && maxWards != null && minWards > maxWards)) {
            "Minimum wards cannot be greater than maximum wards"
        }
        require(!(minPopulation != null && maxPopulation != null && minPopulation > maxPopulation)) {
            "Minimum population cannot be greater than maximum population"
        }
        require(!(minArea != null && maxArea != null && minArea > maxArea)) {
            "Minimum area cannot be greater than maximum area"
        }
        require(!((latitude != null || longitude != null) && radiusKm == null)) {
            "Radius is required when searching by coordinates"
        }
    }
}
