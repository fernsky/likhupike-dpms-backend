package np.gov.mofaga.imis.location.api.dto.mapper

import np.gov.mofaga.imis.location.api.dto.enums.WardField
import np.gov.mofaga.imis.location.api.dto.response.DynamicWardProjection
import np.gov.mofaga.imis.location.api.dto.response.WardDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.WardResponse
import np.gov.mofaga.imis.location.api.dto.response.WardSummaryResponse
import np.gov.mofaga.imis.location.domain.Ward

interface WardMapper {
    fun toResponse(ward: Ward): WardResponse

    fun toDetailResponse(ward: Ward): WardDetailResponse

    fun toSummaryResponse(ward: Ward): WardSummaryResponse

    /**
     * Creates a dynamic projection of Ward entity with selected fields
     * @param ward Source entity
     * @param fields Set of fields to include in projection
     */
    fun toProjection(
        ward: Ward,
        fields: Set<WardField>,
    ): DynamicWardProjection
}
