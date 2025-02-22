package np.gov.mofaga.imis.location.repository.impl

import jakarta.persistence.EntityManager
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.repository.MunicipalityRepository
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
class MunicipalityRepositoryImpl(
    entityManager: EntityManager,
) : SimpleJpaRepository<Municipality, String>(Municipality::class.java, entityManager),
    MunicipalityRepository {
    private val customImpl = CustomMunicipalityRepositoryImpl(entityManager)

    override fun findByDistrictCode(districtCode: String) = customImpl.findByDistrictCode(districtCode)

    override fun findByCodeAndDistrictCode(
        code: String,
        districtCode: String,
    ) = customImpl.findByCodeAndDistrictCode(code, districtCode)

    override fun findNearby(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
        pageable: org.springframework.data.domain.Pageable,
    ) = customImpl.findNearby(latitude, longitude, radiusInMeters, pageable)

    override fun findByTypeAndDistrict(
        type: np.gov.mofaga.imis.location.domain.MunicipalityType,
        districtCode: String,
    ) = customImpl.findByTypeAndDistrict(type, districtCode)

    override fun findLargeMunicipalities(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: org.springframework.data.domain.Pageable,
    ) = customImpl.findLargeMunicipalities(minPopulation, minArea, pageable)

    override fun existsByCodeAndDistrict(
        code: String,
        districtCode: String,
    ) = customImpl.existsByCodeAndDistrict(code, districtCode)

    override fun countByTypeAndDistrict(districtCode: String) = customImpl.countByTypeAndDistrict(districtCode)

    override fun getTotalPopulationByDistrict(districtCode: String) = customImpl.getTotalPopulationByDistrict(districtCode)

    override fun findByMinimumWards(
        minWards: Int,
        pageable: org.springframework.data.domain.Pageable,
    ) = customImpl.findByMinimumWards(minWards, pageable)

    override fun findByCodeIgnoreCase(code: String): Optional<Municipality> = customImpl.findByCodeIgnoreCase(code)

    override fun existsByCodeIgnoreCase(code: String): Boolean = customImpl.existsByCodeIgnoreCase(code)

    override fun findByType(type: np.gov.mofaga.imis.location.domain.MunicipalityType): List<Municipality> =
        customImpl.findByType(type)
}
