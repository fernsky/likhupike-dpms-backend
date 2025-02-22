package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.Province
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProvinceRepository :
    JpaRepository<Province, String>,
    JpaSpecificationExecutor<Province>,
    CustomProvinceRepository
