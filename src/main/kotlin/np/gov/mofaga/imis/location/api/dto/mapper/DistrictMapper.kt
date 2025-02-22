package np.gov.mofaga.imis.location.api.dto.mapper

import np.gov.mofaga.imis.location.api.dto.response.DistrictDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.DistrictResponse
import np.gov.mofaga.imis.location.api.dto.response.DistrictSummaryResponse
import np.gov.mofaga.imis.location.domain.District

interface DistrictMapper {
    /**
     * Maps a District entity to a basic DistrictResponse
     * @throws IllegalArgumentException if required fields are null
     */
    fun toResponse(district: District): DistrictResponse

    /**
     * Maps a District entity to a detailed response with statistics
     * @throws IllegalArgumentException if required fields are null
     */
    fun toDetailResponse(district: District): DistrictDetailResponse

    /**
     * Maps a District entity to a summary response for list views
     * @throws IllegalArgumentException if required fields are null
     */
    fun toSummaryResponse(district: District): DistrictSummaryResponse
}
