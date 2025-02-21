package np.gov.likhupikemun.dpms.location.api.dto.criteria

import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.shared.dto.BaseSearchCriteria
import org.springframework.data.domain.Sort
import java.math.BigDecimal

data class WardSearchCriteria(
    val municipalityCode: String? = null,
    val districtCode: String? = null,
    val provinceCode: String? = null,
    val wardNumber: Int? = null,
    @field:Min(1) @field:Max(33)
    val wardNumberFrom: Int? = null,
    @field:Min(1) @field:Max(33)
    val wardNumberTo: Int? = null,
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
    val sortBy: WardSortField = WardSortField.WARD_NUMBER,
    val sortDirection: Sort.Direction = Sort.Direction.ASC,
    @field:Min(0)
    override val page: Int = 0,
    @field:Min(1)
    override val pageSize: Int = 20,
) : BaseSearchCriteria(page, pageSize) {
    fun validate() {
        require(!(wardNumberFrom != null && wardNumberTo != null && wardNumberFrom > wardNumberTo)) {
            "Starting ward number cannot be greater than ending ward number"
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
        require(!(wardNumber != null && (wardNumberFrom != null || wardNumberTo != null))) {
            "Cannot specify both specific ward number and ward number range"
        }
    }

    fun isGeographicSearch(): Boolean = latitude != null || longitude != null

    fun hasPopulationFilter(): Boolean = minPopulation != null || maxPopulation != null

    fun hasAreaFilter(): Boolean = minArea != null || maxArea != null

    fun hasWardNumberFilter(): Boolean = wardNumber != null || wardNumberFrom != null || wardNumberTo != null

    fun hasHierarchyFilter(): Boolean = municipalityCode != null || districtCode != null || provinceCode != null

    fun getSort(): Sort = Sort.by(sortDirection, sortBy.toEntityField())
}

enum class WardSortField {
    WARD_NUMBER,
    POPULATION,
    AREA,
    CREATED_AT,
    DISTANCE, // For geo-search results
    ;

    fun toEntityField(): String =
        when (this) {
            WARD_NUMBER -> "wardNumber"
            POPULATION -> "population"
            AREA -> "area"
            CREATED_AT -> "createdAt"
            DISTANCE -> "distance" // Handled specially in repository
        }
}
