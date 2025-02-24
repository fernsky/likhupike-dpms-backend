package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.response.DistrictSummaryResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceProjection
import org.geojson.GeoJsonObject
import java.math.BigDecimal

data class TestProvinceProjection(
    override val code: String,
    override val name: String?,
    override val nameNepali: String?,
    override val area: BigDecimal?,
    override val population: Long?,
    override val headquarter: String?,
    override val headquarterNepali: String?,
    override val totalArea: BigDecimal?,
    override val totalPopulation: Long?,
    override val totalMunicipalities: Int?,
    override val districtCount: Int?,
    override val geometry: GeoJsonObject?,
    override val districts: List<DistrictSummaryResponse>?,
) : ProvinceProjection
