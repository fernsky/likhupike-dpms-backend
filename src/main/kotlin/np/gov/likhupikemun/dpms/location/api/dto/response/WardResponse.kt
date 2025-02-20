package np.gov.likhupikemun.dpms.location.api.dto.response

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
    val stats: WardStats,
)

data class WardSummaryResponse(
    val wardNumber: Int,
    val population: Long?,
)
