package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.Ward
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface WardRepository :
    JpaRepository<Ward, String>,
    JpaSpecificationExecutor<Ward>,
    CustomWardRepository
