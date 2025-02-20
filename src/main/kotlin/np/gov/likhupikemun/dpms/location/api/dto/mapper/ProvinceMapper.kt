package np.gov.likhupikemun.dpms.location.api.dto.mapper

import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.Province

interface ProvinceMapper {
    /**
     * Maps a Province entity to a basic ProvinceResponse
     * @throws IllegalArgumentException if required fields are null
     */
    fun toResponse(province: Province): ProvinceResponse

    /**
     * Maps a Province entity to a detailed response with statistics
     * @throws IllegalArgumentException if required fields are null
     */
    fun toDetailResponse(province: Province): ProvinceDetailResponse

    /**
     * Maps a Province entity to a summary response for list views
     * @throws IllegalArgumentException if required fields are null
     */
    fun toSummaryResponse(province: Province): ProvinceSummaryResponse
}
