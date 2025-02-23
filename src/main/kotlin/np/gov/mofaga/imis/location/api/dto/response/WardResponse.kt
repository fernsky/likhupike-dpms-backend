package np.gov.mofaga.imis.location.api.dto.response

import org.geojson.GeoJsonObject
import java.math.BigDecimal
import java.util.*

data class WardResponse(
    val wardNumber: Int,
    val area: BigDecimal?,
    val population: Long?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val officeLocation: String?,
    val officeLocationNepali: String?,
    val municipality: MunicipalitySummaryResponse,
)

data class WardDetailResponse(
    val wardNumber: Int,
    val area: BigDecimal?,
    val population: Long?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val officeLocation: String?,
    val officeLocationNepali: String?,
    val municipality: MunicipalitySummaryResponse,
    val geometry: GeoJsonObject?,
)

data class WardSummaryResponse(
    val wardNumber: Int,
    val population: Long?,
)
