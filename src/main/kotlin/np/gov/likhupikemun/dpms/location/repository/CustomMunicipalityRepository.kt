package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

interface CustomMunicipalityRepository {
    fun findByDistrictCode(districtCode: String): List<Municipality>

    fun findByCodeAndDistrictCode(
        code: String,
        districtCode: String,
    ): Optional<Municipality>

    fun findNearby(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
        pageable: Pageable,
    ): Page<Municipality>

    fun findByTypeAndDistrict(
        type: MunicipalityType,
        districtCode: String,
    ): List<Municipality>

    fun findLargeMunicipalities(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: Pageable,
    ): Page<Municipality>

    fun existsByCodeAndDistrict(
        code: String,
        districtCode: String,
    ): Boolean

    fun countByTypeAndDistrict(districtCode: String): Map<MunicipalityType, Long>

    fun getTotalPopulationByDistrict(districtCode: String): Long?

    fun findByMinimumWards(
        minWards: Int,
        pageable: Pageable,
    ): Page<Municipality>

    fun findByCodeIgnoreCase(code: String): Optional<Municipality>

    fun existsByCodeIgnoreCase(code: String): Boolean

    fun findByType(type: MunicipalityType): List<Municipality>
}
