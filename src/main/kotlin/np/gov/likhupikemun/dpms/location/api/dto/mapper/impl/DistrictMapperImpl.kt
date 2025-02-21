package np.gov.likhupikemun.dpms.location.api.dto.mapper.impl

import np.gov.likhupikemun.dpms.location.api.dto.mapper.DistrictMapper
import np.gov.likhupikemun.dpms.location.api.dto.mapper.LocationSummaryMapper
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.District
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DistrictMapperImpl(
    private val locationSummaryMapper: LocationSummaryMapper,
) : DistrictMapper {
    override fun toResponse(district: District): DistrictResponse {
        validateRequiredFields(district)

        return DistrictResponse(
            code = district.code!!,
            name = district.name!!,
            nameNepali = district.nameNepali!!,
            area = district.area,
            population = district.population,
            headquarter = district.headquarter,
            headquarterNepali = district.headquarterNepali,
            province = locationSummaryMapper.toProvinceSummary(district.province!!),
            municipalityCount = district.municipalities.size,
            totalArea = district.area ?: BigDecimal.ZERO,
            totalPopulation = district.population ?: 0L,
        )
    }

    override fun toDetailResponse(district: District): DistrictDetailResponse {
        validateRequiredFields(district)

        return DistrictDetailResponse(
            code = district.code!!,
            name = district.name!!,
            nameNepali = district.nameNepali!!,
            area = district.area,
            population = district.population,
            headquarter = district.headquarter,
            headquarterNepali = district.headquarterNepali,
            province = locationSummaryMapper.toProvinceSummary(district.province!!),
            municipalities =
                district.municipalities
                    .map { locationSummaryMapper.toMunicipalitySummary(it) },
        )
    }

    override fun toSummaryResponse(district: District): DistrictSummaryResponse = locationSummaryMapper.toDistrictSummary(district)

    private fun validateRequiredFields(district: District) {
        requireNotNull(district.code) { "District code cannot be null" }
        requireNotNull(district.name) { "District name cannot be null" }
        requireNotNull(district.nameNepali) { "District Nepali name cannot be null" }
        requireNotNull(district.province) { "Province cannot be null" }
    }

    companion object {
        /**
         * Maximum length for truncated text fields in summary responses
         */
        private const val MAX_SUMMARY_TEXT_LENGTH = 50
    }
}
