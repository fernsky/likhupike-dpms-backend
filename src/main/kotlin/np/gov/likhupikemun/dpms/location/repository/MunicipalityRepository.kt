package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Municipality
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface MunicipalityRepository :
    JpaRepository<Municipality, String>,
    JpaSpecificationExecutor<Municipality>,
    CustomMunicipalityRepository
