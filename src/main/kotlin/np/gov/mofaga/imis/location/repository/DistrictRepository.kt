package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.District
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface DistrictRepository :
    JpaRepository<District, String>,
    JpaSpecificationExecutor<District>,
    CustomDistrictRepository
