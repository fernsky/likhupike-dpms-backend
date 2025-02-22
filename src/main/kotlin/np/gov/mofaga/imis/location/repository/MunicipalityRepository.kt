package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.Municipality
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface MunicipalityRepository :
    JpaRepository<Municipality, String>,
    JpaSpecificationExecutor<Municipality>,
    CustomMunicipalityRepository
