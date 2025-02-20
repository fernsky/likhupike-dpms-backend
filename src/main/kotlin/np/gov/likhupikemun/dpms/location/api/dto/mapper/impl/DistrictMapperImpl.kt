package np.gov.likhupikemun.dpms.location.api.dto.mapper.impl

import np.gov.likhupikemun.dpms.location.api.dto.mapper.DistrictMapper
import np.gov.likhupikemun.dpms.location.api.dto.mapper.ProvinceMapper
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.District
import org.springframework.stereotype.Component

@Component
class DistrictMapperImpl(
    private val provinceMapper: ProvinceMapper,
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
            province = provinceMapper.toDetailResponse(district.province!!),
            municipalities =
                district.municipalities
                    .sortedBy { it.name }
                    .map { MunicipalityMapper.toSummaryResponse(it) },
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
