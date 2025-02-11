package np.gov.likhupikemun.dpms.family.service

import np.gov.likhupikemun.dpms.family.api.dto.request.CreateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.request.FamilySearchCriteria
import np.gov.likhupikemun.dpms.family.api.dto.request.UpdateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.response.FamilyResponse
import org.springframework.data.domain.Page
import java.util.*

interface FamilyService {
    fun createFamily(request: CreateFamilyRequest): FamilyResponse

    fun updateFamily(
        id: UUID,
        request: UpdateFamilyRequest,
    ): FamilyResponse

    fun getFamily(id: UUID): FamilyResponse

    fun searchFamilies(criteria: FamilySearchCriteria): Page<FamilyResponse>

    fun deleteFamily(id: UUID)
}
