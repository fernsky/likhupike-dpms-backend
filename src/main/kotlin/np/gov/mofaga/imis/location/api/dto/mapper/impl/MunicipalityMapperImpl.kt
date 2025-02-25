package np.gov.mofaga.imis.location.api.dto.mapper.impl

import np.gov.mofaga.imis.location.api.dto.mapper.DistrictMapper
import np.gov.mofaga.imis.location.api.dto.mapper.MunicipalityMapper
import np.gov.mofaga.imis.location.api.dto.response.MunicipalityDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.MunicipalityResponse
import np.gov.mofaga.imis.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import np.gov.mofaga.imis.location.api.dto.enums.MunicipalityField
import np.gov.mofaga.imis.location.api.dto.response.DynamicMunicipalityProjection

@Component
class MunicipalityMapperImpl(
    private val districtMapper: DistrictMapper,
    private val geometryConverter: GeometryConverter,
) : MunicipalityMapper {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun toResponse(municipality: Municipality): MunicipalityResponse {
        validateRequiredFields(municipality)
        logger.debug(
            "Municipality details: code=${municipality.code}, name=${municipality.name}, " +
                "nameNepali=${municipality.nameNepali}, type=${municipality.type}, area=${municipality.area}, " +
                "population=${municipality.population}, latitude=${municipality.latitude}, " +
                "longitude=${municipality.longitude}, totalWards=${municipality.totalWards}, " +
                "district=${municipality.district?.code}",
        )

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
            geometry = geometryConverter.convertToGeoJson(municipality.geometry),
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

    override fun toProjection(municipality: Municipality, fields: Set<MunicipalityField>): DynamicMunicipalityProjection {
        validateRequiredFields(municipality)
        return DynamicMunicipalityProjection.from(municipality, fields, geometryConverter)
    }

    private fun validateRequiredFields(municipality: Municipality) {
        requireNotNull(municipality.code) { "Municipality code cannot be null" }
        requireNotNull(municipality.name) { "Municipality name cannot be null" }
        requireNotNull(municipality.nameNepali) { "Municipality Nepali name cannot be null" }
        requireNotNull(municipality.type) { "Municipality type cannot be null" }
        requireNotNull(municipality.district) { "District cannot be null" }
    }
}
