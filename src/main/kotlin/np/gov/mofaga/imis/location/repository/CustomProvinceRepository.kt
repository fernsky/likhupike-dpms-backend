package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.Province
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

interface CustomProvinceRepository {
    fun findByCodeIgnoreCase(code: String): Optional<Province>

    fun findLargeProvinces(
        minArea: BigDecimal,
        minPopulation: Long,
        pageable: Pageable,
    ): Page<Province>

    fun existsByCode(code: String): Boolean
}
