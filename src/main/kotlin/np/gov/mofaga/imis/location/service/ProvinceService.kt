package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.mofaga.imis.location.api.dto.request.CreateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.response.ProvinceDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceResponse
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface ProvinceService {
    /**
     * Creates a new province
     * @throws ProvinceCodeExistsException if code already exists
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    fun createProvince(request: CreateProvinceRequest): ProvinceResponse

    /**
     * Updates an existing province
     * @throws ProvinceNotFoundException if province not found
     * @throws InvalidOperationException if operation not allowed
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    fun updateProvince(
        code: String,
        request: UpdateProvinceRequest,
    ): ProvinceResponse

    /**
     * Gets detailed province information with statistics
     * @throws ProvinceNotFoundException if province not found
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getProvinceDetail(code: String): ProvinceDetailResponse

    /**
     * Gets basic province information
     * @throws ProvinceNotFoundException if province not found
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getProvince(code: String): ProvinceResponse

    /**
     * Searches provinces based on various criteria
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun searchProvinces(criteria: ProvinceSearchCriteria): Page<ProvinceResponse>

    /**
     * Lists all provinces
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getAllProvinces(): List<ProvinceResponse>

    /**
     * Finds large provinces based on area and population
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun findLargeProvinces(
        minArea: BigDecimal,
        minPopulation: Long,
        page: Int,
        size: Int,
    ): Page<ProvinceResponse>

    /**
     * Validates province existence
     * @throws ProvinceNotFoundException if province not found
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun validateProvinceExists(code: String)
}
