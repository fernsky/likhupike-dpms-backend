package np.gov.mofaga.imis.location.api.dto.mapper

import np.gov.mofaga.imis.location.api.dto.response.MunicipalityDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.MunicipalityResponse
import np.gov.mofaga.imis.location.api.dto.response.MunicipalitySummaryResponse
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
}
