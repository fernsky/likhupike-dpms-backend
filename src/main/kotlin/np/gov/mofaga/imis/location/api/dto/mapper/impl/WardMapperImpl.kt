package np.gov.mofaga.imis.location.api.dto.mapper.impl

import np.gov.mofaga.imis.location.api.dto.mapper.MunicipalityMapper
import np.gov.mofaga.imis.location.api.dto.mapper.WardMapper
import np.gov.mofaga.imis.location.api.dto.response.WardDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.WardResponse
import np.gov.mofaga.imis.location.api.dto.response.WardSummaryResponse
import np.gov.mofaga.imis.location.domain.Ward
import org.springframework.stereotype.Component

@Component
class WardMapperImpl(
    private val municipalityMapper: MunicipalityMapper,
) : WardMapper {
    override fun toResponse(ward: Ward): WardResponse {
        require(ward.wardNumber != null) { "Ward number cannot be null" }
        require(ward.municipality != null) { "Municipality cannot be null" }

        return WardResponse(
            wardNumber = ward.wardNumber!!,
            area = ward.area,
            population = ward.population,
            latitude = ward.latitude,
            longitude = ward.longitude,
            officeLocation = ward.officeLocation,
            officeLocationNepali = ward.officeLocationNepali,
            municipality = municipalityMapper.toSummaryResponse(ward.municipality!!),
        )
    }

    override fun toDetailResponse(ward: Ward): WardDetailResponse {
        require(ward.wardNumber != null) { "Ward number cannot be null" }
        require(ward.municipality != null) { "Municipality cannot be null" }

        return WardDetailResponse(
            wardNumber = ward.wardNumber!!,
            area = ward.area,
            population = ward.population,
            latitude = ward.latitude,
            longitude = ward.longitude,
            officeLocation = ward.officeLocation,
            officeLocationNepali = ward.officeLocationNepali,
            municipality = municipalityMapper.toSummaryResponse(ward.municipality!!),
        )
    }

    override fun toSummaryResponse(ward: Ward): WardSummaryResponse {
        require(ward.wardNumber != null) { "Ward number cannot be null" }

        return WardSummaryResponse(
            wardNumber = ward.wardNumber!!,
            population = ward.population,
        )
    }
}
