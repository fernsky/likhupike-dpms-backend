package np.gov.likhupikemun.dpms.location.api.dto.mapper

import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictSummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Province

interface LocationSummaryMapper {
    fun toDistrictSummary(district: District): DistrictSummaryResponse

    fun toProvinceSummary(province: Province): ProvinceSummaryResponse

    fun toMunicipalitySummary(municipality: Municipality): MunicipalitySummaryResponse
}
