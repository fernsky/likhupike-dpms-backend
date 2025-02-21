package np.gov.likhupikemun.dpms.location.api.dto.mapper.impl

import np.gov.likhupikemun.dpms.location.api.dto.mapper.DistrictMapper
import np.gov.likhupikemun.dpms.location.api.dto.mapper.MunicipalityMapper
import np.gov.likhupikemun.dpms.location.api.dto.mapper.ProvinceMapper
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.District
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DistrictMapperImpl(
    private val provinceMapper: ProvinceMapper,
    private val municipalityMapper: MunicipalityMapper,
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
            province = provinceMapper.toSummaryResponse(district.province!!),
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
            province = provinceMapper.toSummaryResponse(district.province!!),
            municipalities =
                district.municipalities
                    .sortedBy { it.name }
                    .map { municipalityMapper.toSummaryResponse(it) },
        )
    }

    override fun toSummaryResponse(district: District): DistrictSummaryResponse {
        validateRequiredFields(district)

        return DistrictSummaryResponse(
            code = district.code!!,
            name = district.name!!,
            nameNepali = district.nameNepali!!,
            municipalityCount = district.municipalities.size,
        )
    }

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
