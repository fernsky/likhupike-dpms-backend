package np.gov.likhupikemun.dpms.location.api.dto.mapper.impl

import np.gov.likhupikemun.dpms.location.api.dto.mapper.DistrictMapper
import np.gov.likhupikemun.dpms.location.api.dto.mapper.MunicipalityMapper
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.likhupikemun.dpms.location.domain.Municipality
import org.springframework.stereotype.Component

@Component
class MunicipalityMapperImpl(
    private val districtMapper: DistrictMapper,
) : MunicipalityMapper {
    override fun toResponse(municipality: Municipality): MunicipalityResponse {
        validateRequiredFields(municipality)

        return MunicipalityResponse(
            code = municipality.code!!,
            name = municipality.name!!,
            nameNepali = municipality.nameNepali!!,
            type = municipality.type!!,
            area = municipality.area,
            population = municipality.population,
            latitude = municipality.latitude,
            longitude = municipality.longitude,
            totalWards = municipality.totalWards ?: 0,
            district = districtMapper.toSummaryResponse(municipality.district!!),
        )
    }

    override fun toDetailResponse(municipality: Municipality): MunicipalityDetailResponse {
        validateRequiredFields(municipality)

        return MunicipalityDetailResponse(
            code = municipality.code!!,
            name = municipality.name!!,
            nameNepali = municipality.nameNepali!!,
            type = municipality.type!!,
            area = municipality.area,
            population = municipality.population,
            latitude = municipality.latitude,
            longitude = municipality.longitude,
            totalWards = municipality.totalWards ?: 0,
            district = districtMapper.toDetailResponse(municipality.district!!),
        )
    }

    override fun toSummaryResponse(municipality: Municipality): MunicipalitySummaryResponse {
        validateRequiredFields(municipality)

        return MunicipalitySummaryResponse(
            code = municipality.code!!,
            name = municipality.name!!,
            nameNepali = municipality.nameNepali!!,
            type = municipality.type!!,
            totalWards = municipality.totalWards ?: 0,
        )
    }

    private fun validateRequiredFields(municipality: Municipality) {
        requireNotNull(municipality.code) { "Municipality code cannot be null" }
        requireNotNull(municipality.name) { "Municipality name cannot be null" }
        requireNotNull(municipality.nameNepali) { "Municipality Nepali name cannot be null" }
        requireNotNull(municipality.type) { "Municipality type cannot be null" }
        requireNotNull(municipality.district) { "District cannot be null" }
    }
}
