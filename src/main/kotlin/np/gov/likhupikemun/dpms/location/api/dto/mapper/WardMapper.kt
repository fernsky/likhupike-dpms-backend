package np.gov.likhupikemun.dpms.location.api.dto.mapper

import np.gov.likhupikemun.dpms.location.api.dto.response.WardDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.WardResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.WardSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.Ward

interface WardMapper {
    fun toResponse(ward: Ward): WardResponse

    fun toDetailResponse(ward: Ward): WardDetailResponse

    fun toSummaryResponse(ward: Ward): WardSummaryResponse
}
