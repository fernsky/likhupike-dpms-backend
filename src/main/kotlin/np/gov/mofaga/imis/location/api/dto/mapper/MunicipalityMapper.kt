package np.gov.mofaga.imis.location.api.dto.mapper

import np.gov.mofaga.imis.location.api.dto.enums.MunicipalityField
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.Municipality

interface MunicipalityMapper {
    /**
     * Converts Municipality entity to basic response DTO
     * @throws IllegalArgumentException if required fields are null
     */
    fun toResponse(municipality: Municipality): MunicipalityResponse

    /**
     * Converts Municipality entity to detailed response with statistics
     * @throws IllegalArgumentException if required fields are null
     */
    fun toDetailResponse(municipality: Municipality): MunicipalityDetailResponse

    /**
     * Converts Municipality entity to summary response for list views
     * @throws IllegalArgumentException if required fields are null
     */
    fun toSummaryResponse(municipality: Municipality): MunicipalitySummaryResponse

    /**
     * Creates a dynamic projection of Municipality entity with selected fields
     * @param municipality Source entity
     * @param fields Set of fields to include in projection
     */
    fun toProjection(
        municipality: Municipality,
        fields: Set<MunicipalityField>,
    ): DynamicMunicipalityProjection
}
