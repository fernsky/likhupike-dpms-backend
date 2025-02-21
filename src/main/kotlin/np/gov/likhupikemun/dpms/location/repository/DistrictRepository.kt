package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.District
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface DistrictRepository :
    JpaRepository<District, String>,
    JpaSpecificationExecutor<District>,
    CustomDistrictRepository
