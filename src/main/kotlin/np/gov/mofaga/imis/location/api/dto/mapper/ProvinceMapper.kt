package np.gov.mofaga.imis.location.api.dto.mapper

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.api.dto.response.DynamicProvinceProjection
import np.gov.mofaga.imis.location.api.dto.response.ProvinceDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceSummaryResponse
import np.gov.mofaga.imis.location.domain.Province

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

    /**
     * Creates a dynamic projection of Province entity with selected fields
     * @param province Source entity
     * @param fields Set of fields to include in projection
     */
    fun toProjection(
        province: Province,
        fields: Set<ProvinceField>,
    ): DynamicProvinceProjection // Updated return type
}
