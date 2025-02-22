package np.gov.mofaga.imis.family.repository

import np.gov.mofaga.imis.family.domain.Family
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface FamilyRepository :
    JpaRepository<Family, UUID>,
    JpaSpecificationExecutor<Family>
