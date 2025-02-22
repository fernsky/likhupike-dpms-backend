package np.gov.mofaga.imis.location.repository.impl

import jakarta.persistence.EntityManager
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.repository.DistrictRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
class DistrictRepositoryImpl(
    entityManager: EntityManager,
) : SimpleJpaRepository<District, String>(District::class.java, entityManager),
    DistrictRepository {
    private val customImpl = CustomDistrictRepositoryImpl(entityManager)

    // Delegate custom methods to customImpl
    override fun findByProvinceCode(provinceCode: String) = customImpl.findByProvinceCode(provinceCode)

    override fun findByCode(code: String) = customImpl.findByCode(code)

    override fun findByCodeIgnoreCase(code: String) = customImpl.findByCodeIgnoreCase(code)

    override fun existsByCode(code: String) = customImpl.existsByCode(code)

    override fun existsByCodeAndProvince(
        code: String,
        provinceCode: String,
    ) = customImpl.existsByCodeAndProvince(code, provinceCode)

    override fun findNearbyDistricts(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
        pageable: Pageable,
    ) = customImpl.findNearbyDistricts(latitude, longitude, radiusInMeters, pageable)

    override fun findLargeDistricts(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: Pageable,
    ) = customImpl.findLargeDistricts(minPopulation, minArea, pageable)
}
