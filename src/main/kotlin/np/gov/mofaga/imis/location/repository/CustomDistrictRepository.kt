package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.District
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

interface CustomDistrictRepository {
    fun findByProvinceCode(provinceCode: String): List<District>

    fun findByCode(code: String): Optional<District>

    fun findByCodeIgnoreCase(code: String): Optional<District>

    fun existsByCode(code: String): Boolean

    fun existsByCodeAndProvince(
        code: String,
        provinceCode: String,
    ): Boolean

    fun findNearbyDistricts(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
        pageable: Pageable,
    ): Page<District>

    fun findLargeDistricts(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: Pageable,
    ): Page<District>
}
