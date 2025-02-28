package np.gov.mofaga.imis.family.repository

import np.gov.mofaga.imis.family.domain.FamilyPhoto
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FamilyPhotoRepository : JpaRepository<FamilyPhoto, UUID> {
    fun findByFamilyId(familyId: UUID): List<FamilyPhoto>
}
