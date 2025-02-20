package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Province
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

interface ProvinceRepository {
    fun findByCodeIgnoreCase(code: String): Optional<Province>

    fun findLargeProvinces(
        minArea: BigDecimal,
        minPopulation: Long,
        pageable: Pageable,
    ): Page<Province>

    fun existsByCode(
        code: String,
        excludeId: UUID?,
    ): Boolean
}
