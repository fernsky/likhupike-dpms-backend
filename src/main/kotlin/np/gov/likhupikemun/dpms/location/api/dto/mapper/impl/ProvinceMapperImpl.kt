package np.gov.likhupikemun.dpms.location.api.dto.mapper.impl

import np.gov.likhupikemun.dpms.location.api.dto.mapper.DistrictMapper
import np.gov.likhupikemun.dpms.location.api.dto.mapper.ProvinceMapper
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.Province
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProvinceMapperImpl(
    private val districtMapper: DistrictMapper,
) : ProvinceMapper {
    override fun toResponse(province: Province): ProvinceResponse {
        validateRequiredFields(province)

        val totalPopulation = province.district.sumOf { it.population ?: 0L }
        val totalArea =
            province.district
                .mapNotNull { it.area }
                .fold(BigDecimal.ZERO) { acc, area -> acc.add(area) }

        return ProvinceResponse(
            code = province.code!!,
            name = province.name!!,
            nameNepali = province.nameNepali!!,
            area = province.area,
            population = province.population,
            headquarter = province.headquarter,
            headquarterNepali = province.headquarterNepali,
            districtCount = province.district.size,
            totalPopulation = totalPopulation,
            totalArea = totalArea,
        )
    }

    override fun toDetailResponse(province: Province): ProvinceDetailResponse {
        validateRequiredFields(province)

        val districts =
            province.districts
                .sortedBy { it.name }
                .map { districtMapper.toSummaryResponse(it) }

        return ProvinceDetailResponse(
            code = province.code!!,
            name = province.name!!,
            nameNepali = province.nameNepali!!,
            area = province.area,
            population = province.population,
            headquarter = province.headquarter,
            headquarterNepali = province.headquarterNepali,
            districts = districts,
        )
    }

    override fun toSummaryResponse(province: Province): ProvinceSummaryResponse {
        validateRequiredFields(province)

        return ProvinceSummaryResponse(
            id = province.id!!,
            name = province.name!!,
            nameNepali = province.nameNepali!!,
            code = province.code!!,
        )
    }

    private fun validateRequiredFields(province: Province) {
        requireNotNull(province.id) { "Province ID cannot be null" }
        requireNotNull(province.name) { "Province name cannot be null" }
        requireNotNull(province.nameNepali) { "Province Nepali name cannot be null" }
        requireNotNull(province.code) { "Province code cannot be null" }
    }
}
