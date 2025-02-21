package np.gov.likhupikemun.dpms.location.service

import np.gov.likhupikemun.dpms.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictResponse
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface DistrictService {
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    fun createDistrict(request: CreateDistrictRequest): DistrictResponse

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    fun updateDistrict(
        code: String,
        request: UpdateDistrictRequest,
    ): DistrictResponse

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getDistrictDetail(code: String): DistrictDetailResponse

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getDistrict(code: String): DistrictResponse

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun searchDistricts(criteria: DistrictSearchCriteria): Page<DistrictResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getAllDistricts(): List<DistrictResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getDistrictsByProvince(provinceCode: String): List<DistrictResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun findLargeDistricts(
        minArea: BigDecimal,
        minPopulation: Long,
        page: Int,
        size: Int,
    ): Page<DistrictResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun validateDistrictExists(code: String)

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun findNearbyDistricts(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): Page<DistrictResponse>
}
