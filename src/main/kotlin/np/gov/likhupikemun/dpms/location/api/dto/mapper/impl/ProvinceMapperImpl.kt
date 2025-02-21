package np.gov.likhupikemun.dpms.location.api.dto.mapper.impl

import np.gov.likhupikemun.dpms.location.api.dto.mapper.LocationSummaryMapper
import np.gov.likhupikemun.dpms.location.api.dto.mapper.ProvinceMapper
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.Province
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProvinceMapperImpl(
    private val locationSummaryMapper: LocationSummaryMapper,
) : ProvinceMapper {
    override fun toResponse(province: Province): ProvinceResponse {
        validateRequiredFields(province)

        val totalPopulation = province.districts.sumOf { it.population ?: 0L }
        val totalArea =
            province.districts
                .mapNotNull { it.area }
                .fold(BigDecimal.ZERO) { acc: BigDecimal, area: BigDecimal -> acc.add(area) }

        return ProvinceResponse(
            code = province.code!!,
            name = province.name!!,
            nameNepali = province.nameNepali!!,
            area = province.area,
            population = province.population,
            headquarter = province.headquarter,
            headquarterNepali = province.headquarterNepali,
            districtCount = province.districts.size,
            totalPopulation = totalPopulation,
            totalArea = totalArea,
        )
    }

    override fun toDetailResponse(province: Province): ProvinceDetailResponse {
        validateRequiredFields(province)

        return ProvinceDetailResponse(
            code = province.code!!,
            name = province.name!!,
            nameNepali = province.nameNepali!!,
            area = province.area,
            population = province.population,
            headquarter = province.headquarter,
            headquarterNepali = province.headquarterNepali,
            districts =
                province.districts
                    .sortedBy { it.name }
                    .map { locationSummaryMapper.toDistrictSummary(it) },
        )
    }

    override fun toSummaryResponse(province: Province): ProvinceSummaryResponse = locationSummaryMapper.toProvinceSummary(province)

    private fun validateRequiredFields(province: Province) {
        requireNotNull(province.code) { "Province code cannot be null" }
        requireNotNull(province.name) { "Province name cannot be null" }
        requireNotNull(province.nameNepali) { "Province Nepali name cannot be null" }
    }
}
