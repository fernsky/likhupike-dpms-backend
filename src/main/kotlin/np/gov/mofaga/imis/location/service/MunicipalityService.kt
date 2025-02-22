package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.mofaga.imis.location.api.dto.request.CreateMunicipalityRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.mofaga.imis.location.api.dto.response.MunicipalityDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.MunicipalityResponse
import np.gov.mofaga.imis.location.domain.MunicipalityType
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

interface MunicipalityService {
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    fun createMunicipality(request: CreateMunicipalityRequest): MunicipalityResponse

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    fun updateMunicipality(
        code: String,
        request: UpdateMunicipalityRequest,
    ): MunicipalityResponse

    @Transactional(readOnly = true)
    fun getMunicipalityDetail(code: String): MunicipalityDetailResponse

    @Transactional(readOnly = true)
    fun getMunicipality(code: String): MunicipalityResponse

    @Transactional(readOnly = true)
    fun searchMunicipalities(criteria: MunicipalitySearchCriteria): Page<MunicipalityResponse>

    @Transactional(readOnly = true)
    fun getMunicipalitiesByDistrict(districtCode: String): List<MunicipalityResponse>

    @Transactional(readOnly = true)
    fun findNearbyMunicipalities(criteria: MunicipalitySearchCriteria): Page<MunicipalityResponse>

    @Transactional(readOnly = true)
    fun getMunicipalitiesByType(type: MunicipalityType): List<MunicipalityResponse>

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    fun validateMunicipalityExists(code: String)

    @Transactional(readOnly = true)
    fun getAllMunicipalities(): List<MunicipalityResponse>

    fun validateMunicipalityAccess(municipalityCode: String)
}
