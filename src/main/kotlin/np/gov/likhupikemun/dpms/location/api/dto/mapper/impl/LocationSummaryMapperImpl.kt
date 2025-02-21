package np.gov.likhupikemun.dpms.location.api.dto.mapper.impl

import np.gov.likhupikemun.dpms.location.api.dto.mapper.LocationSummaryMapper
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictSummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Province
import org.springframework.stereotype.Component

@Component
class LocationSummaryMapperImpl : LocationSummaryMapper {
    override fun toDistrictSummary(district: District): DistrictSummaryResponse {
        requireNotNull(district.code) { "District code cannot be null" }
        requireNotNull(district.name) { "District name cannot be null" }
        requireNotNull(district.nameNepali) { "District Nepali name cannot be null" }

        return DistrictSummaryResponse(
            code = district.code!!,
            name = district.name!!,
            nameNepali = district.nameNepali!!,
            municipalityCount = district.municipalities.size,
        )
    }

    override fun toProvinceSummary(province: Province): ProvinceSummaryResponse {
        requireNotNull(province.code) { "Province code cannot be null" }
        requireNotNull(province.name) { "Province name cannot be null" }
        requireNotNull(province.nameNepali) { "Province Nepali name cannot be null" }

        return ProvinceSummaryResponse(
            code = province.code!!,
            name = province.name!!,
            nameNepali = province.nameNepali!!,
        )
    }

    override fun toMunicipalitySummary(municipality: Municipality): MunicipalitySummaryResponse {
        requireNotNull(municipality.code) { "Municipality code cannot be null" }
        requireNotNull(municipality.name) { "Municipality name cannot be null" }
        requireNotNull(municipality.nameNepali) { "Municipality Nepali name cannot be null" }
        requireNotNull(municipality.type) { "Municipality type cannot be null" }

        return MunicipalitySummaryResponse(
            code = municipality.code!!,
            name = municipality.name!!,
            nameNepali = municipality.nameNepali!!,
            type = municipality.type!!,
            totalWards = municipality.totalWards ?: 0,
        )
    }
}
