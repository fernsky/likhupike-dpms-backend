package np.gov.likhupikemun.dpms.location.api.dto.response

import java.math.BigDecimal
import java.util.*

data class ProvinceResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val area: BigDecimal?,
    val population: Long?,
    val headquarter: String?,
    val headquarterNepali: String?,
    val districtCount: Int,
    val totalPopulation: Long?,
    val totalArea: BigDecimal?,
)

data class ProvinceDetailResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
    val area: BigDecimal?,
    val population: Long?,
    val headquarter: String?,
    val headquarterNepali: String?,
    val districts: List<DistrictSummaryResponse>,
)

data class ProvinceSummaryResponse(
    val code: String,
    val name: String,
    val nameNepali: String,
)
