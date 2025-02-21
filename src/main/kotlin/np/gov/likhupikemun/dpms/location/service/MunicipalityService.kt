package np.gov.likhupikemun.dpms.location.service

import np.gov.likhupikemun.dpms.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityResponse
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface MunicipalityService {
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun createMunicipality(request: CreateMunicipalityRequest): MunicipalityResponse

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun updateMunicipality(
        code: String,
        request: UpdateMunicipalityRequest,
    ): MunicipalityResponse

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getMunicipalityDetail(code: String): MunicipalityDetailResponse

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getMunicipality(code: String): MunicipalityResponse

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun searchMunicipalities(criteria: MunicipalitySearchCriteria): Page<MunicipalityResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getMunicipalitiesByDistrict(districtCode: String): List<MunicipalityResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun findNearbyMunicipalities(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): Page<MunicipalityResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getMunicipalitiesByType(type: MunicipalityType): List<MunicipalityResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun validateMunicipalityExists(code: String)

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun getAllMunicipalities(): List<MunicipalityResponse>

    fun validateMunicipalityAccess(municipalityCode: String)
}
