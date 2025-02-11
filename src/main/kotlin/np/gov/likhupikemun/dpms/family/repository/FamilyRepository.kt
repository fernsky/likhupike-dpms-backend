package np.gov.likhupikemun.dpms.family.repository

import np.gov.likhupikemun.dpms.family.domain.Family
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface FamilyRepository :
    JpaRepository<Family, UUID>,
    JpaSpecificationExecutor<Family>
