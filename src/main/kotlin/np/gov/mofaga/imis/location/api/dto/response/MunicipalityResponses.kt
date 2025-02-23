package np.gov.mofaga.imis.location.api.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import np.gov.mofaga.imis.location.domain.MunicipalityType
import org.geojson.GeoJsonObject
import java.math.BigDecimal
import java.util.*

@Schema(description = "Full municipality response with all details")
data class MunicipalityResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val type: MunicipalityType,
    val area: BigDecimal?,
    val population: Long?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val totalWards: Int,
    val district: DistrictSummaryResponse,
)

@Schema(description = "Detailed municipality response with statistics")
data class MunicipalityDetailResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val type: MunicipalityType,
    val area: BigDecimal?,
    val population: Long?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val totalWards: Int,
    val district: DistrictDetailResponse,
    val geometry: GeoJsonObject?,
)

@Schema(description = "Summary response for municipality listings")
data class MunicipalitySummaryResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val type: MunicipalityType,
    val totalWards: Int,
)
