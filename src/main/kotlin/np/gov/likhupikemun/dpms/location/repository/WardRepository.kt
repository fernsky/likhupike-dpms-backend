package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Ward
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface WardRepository :
    JpaRepository<Ward, String>,
    JpaSpecificationExecutor<Ward>,
    CustomWardRepository
