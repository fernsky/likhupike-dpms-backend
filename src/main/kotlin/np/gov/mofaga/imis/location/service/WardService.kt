package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.location.api.dto.criteria.WardSearchCriteria
import np.gov.mofaga.imis.location.api.dto.request.CreateWardRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateWardRequest
import np.gov.mofaga.imis.location.api.dto.response.WardDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.WardResponse
import np.gov.mofaga.imis.location.api.dto.response.WardSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface WardService {
    /**
     * Creates a new ward
     * @throws MunicipalityNotFoundException if municipality not found
     * @throws DuplicateWardNumberException if ward number already exists in municipality
     */
    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    @Transactional
    fun createWard(request: CreateWardRequest): WardResponse

    /**
     * Updates an existing ward
     * @throws WardNotFoundException if ward not found
     * @throws InvalidWardOperationException if user doesn't have access to ward
     */
    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    @Transactional
    fun updateWard(
        wardNumber: Int,
        municipalityCode: String,
        request: UpdateWardRequest,
    ): WardResponse

    /**
     * Gets detailed ward information including statistics
     * @throws WardNotFoundException if ward not found
     */

    @Transactional(readOnly = true)
    fun getWardDetail(
        wardNumber: Int,
        municipalityCode: String,
    ): WardDetailResponse

    /**
     * Gets basic ward information
     * @throws WardNotFoundException if ward not found
     */

    @Transactional(readOnly = true)
    fun getWard(
        wardNumber: Int,
        municipalityCode: String,
    ): WardResponse

    /**
     * Searches wards based on various criteria
     * No authentication required for search
     */
    @Transactional(readOnly = true)
    fun searchWards(criteria: WardSearchCriteria): Page<WardResponse>

    /**
     * Lists all wards in a municipality
     * No authentication required
     */
    @Transactional(readOnly = true)
    fun getWardsByMunicipality(municipalityCode: String): List<WardSummaryResponse>

    /**
     * Finds nearby wards within specified radius
     * No authentication required
     */
    @Transactional(readOnly = true)
    fun findNearbyWards(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): Page<WardSummaryResponse>

    /**
     * Validates if ward exists and user has access
     * @throws WardNotFoundException if ward not found
     * @throws InvalidWardOperationException if user doesn't have access
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun validateWardExists(
        wardNumber: Int,
        municipalityCode: String,
    )
}
