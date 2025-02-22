package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.Ward
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface CustomWardRepository {
    fun findByMunicipalityCode(municipalityCode: String): List<Ward>

    fun findByDistrictCode(districtCode: String): List<Ward>

    fun findByProvinceCode(provinceCode: String): List<Ward>

    fun findByWardNumberAndMunicipalityCode(
        wardNumber: Int,
        municipalityCode: String,
    ): Optional<Ward>

    fun findByWardNumberRange(
        municipalityCode: String,
        fromWard: Int,
        toWard: Int,
    ): List<Ward>

    fun existsByWardNumberAndMunicipality(
        wardNumber: Int,
        municipalityCode: String,
    ): Boolean

    fun findByPopulationRange(
        minPopulation: Long,
        maxPopulation: Long,
        pageable: Pageable,
    ): Page<Ward>

    fun countByMunicipalityCode(municipalityCode: String): Int
}
