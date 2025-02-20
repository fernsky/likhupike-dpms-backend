package np.gov.likhupikemun.dpms.location.api.dto.response

import java.math.BigDecimal
import java.util.*

data class DistrictResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val area: BigDecimal?,
    val population: Long?,
    val headquarter: String?,
    val headquarterNepali: String?,
    val province: ProvinceSummaryResponse,
    val municipalityCount: Int,
    val totalPopulation: Long?,
    val totalArea: BigDecimal?,
)

data class DistrictDetailResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val area: BigDecimal?,
    val population: Long?,
    val headquarter: String?,
    val headquarterNepali: String?,
    val province: ProvinceSummaryResponse,
    val municipalities: List<MunicipalitySummaryResponse>,
    val stats: DistrictStats,
)

data class DistrictSummaryResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val municipalityCount: Int,
)
