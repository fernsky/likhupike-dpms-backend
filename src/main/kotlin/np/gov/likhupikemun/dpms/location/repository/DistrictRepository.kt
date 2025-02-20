package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.District
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

interface CustomDistrictRepository {
    fun findByProvinceCode(provinceCode: String): List<District>

    fun findByCodeAndProvinceId(
        code: String,
        provinceId: UUID,
    ): Optional<District>

    fun findLargeDistricts(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: Pageable,
    ): Page<District>

    fun countActiveByProvince(provinceId: UUID): Long

    fun existsByCodeAndProvince(
        code: String,
        provinceId: UUID,
        excludeId: UUID?,
    ): Boolean

    fun findByMinimumMunicipalities(
        minMunicipalities: Int,
        pageable: Pageable,
    ): Page<District>

    fun findNearbyDistricts(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
        pageable: Pageable,
    ): Page<District>

    fun hasActiveDistricts(provinceId: UUID): Boolean

    fun findActiveDistrictsWithoutActiveMunicipalities(pageable: Pageable): Page<District>
}
