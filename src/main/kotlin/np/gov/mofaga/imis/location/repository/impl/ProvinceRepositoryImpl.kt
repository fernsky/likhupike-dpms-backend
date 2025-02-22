package np.gov.mofaga.imis.location.repository.impl

import jakarta.persistence.EntityManager
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class ProvinceRepositoryImpl(
    entityManager: EntityManager,
) : SimpleJpaRepository<Province, String>(Province::class.java, entityManager),
    ProvinceRepository {
    private val customImpl = CustomProvinceRepositoryImpl(entityManager)

    override fun findByCodeIgnoreCase(code: String) = customImpl.findByCodeIgnoreCase(code)

    override fun findLargeProvinces(
        minArea: BigDecimal,
        minPopulation: Long,
        pageable: org.springframework.data.domain.Pageable,
    ) = customImpl.findLargeProvinces(minArea, minPopulation, pageable)

    override fun existsByCode(code: String) = customImpl.existsByCode(code)
}
