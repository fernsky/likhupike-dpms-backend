package np.gov.likhupikemun.dpms.location.repository.impl

import jakarta.persistence.EntityManager
import np.gov.likhupikemun.dpms.location.domain.Ward
import np.gov.likhupikemun.dpms.location.repository.WardRepository
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.stereotype.Repository

@Repository
class WardRepositoryImpl(
    entityManager: EntityManager,
) : SimpleJpaRepository<Ward, String>(Ward::class.java, entityManager),
    WardRepository {
    private val customImpl = CustomWardRepositoryImpl(entityManager)

    override fun findByMunicipalityCode(municipalityCode: String) = customImpl.findByMunicipalityCode(municipalityCode)

    override fun findByDistrictCode(districtCode: String) = customImpl.findByDistrictCode(districtCode)

    override fun findByProvinceCode(provinceCode: String) = customImpl.findByProvinceCode(provinceCode)

    override fun findByWardNumberAndMunicipalityCode(
        wardNumber: Int,
        municipalityCode: String,
    ) = customImpl.findByWardNumberAndMunicipalityCode(wardNumber, municipalityCode)

    override fun findByWardNumberRange(
        municipalityCode: String,
        fromWard: Int,
        toWard: Int,
    ) = customImpl.findByWardNumberRange(municipalityCode, fromWard, toWard)

    override fun existsByWardNumberAndMunicipality(
        wardNumber: Int,
        municipalityCode: String,
    ) = customImpl.existsByWardNumberAndMunicipality(wardNumber, municipalityCode)

    override fun findByPopulationRange(
        minPopulation: Long,
        maxPopulation: Long,
        pageable: org.springframework.data.domain.Pageable,
    ) = customImpl.findByPopulationRange(minPopulation, maxPopulation, pageable)

    override fun countByMunicipalityCode(municipalityCode: String) = customImpl.countByMunicipalityCode(municipalityCode)
}
