package np.gov.likhupikemun.dpms.family.api.dto.request

import np.gov.likhupikemun.dpms.family.domain.enums.SocialCategory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class FamilySearchCriteria(
    val headOfFamily: String? = null,
    val wardNumber: Int? = null,
    val socialCategory: SocialCategory? = null,
    val hasWaterTreatment: Boolean? = null,
    val hasElectricity: Boolean? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "createdAt",
    val sortDirection: Sort.Direction = Sort.Direction.DESC,
) {
    fun toPageable(): Pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy))
}
