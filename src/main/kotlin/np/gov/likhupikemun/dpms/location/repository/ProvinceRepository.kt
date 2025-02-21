package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Province
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProvinceRepository :
    JpaRepository<Province, String>,
    JpaSpecificationExecutor<Province>,
    CustomProvinceRepository
