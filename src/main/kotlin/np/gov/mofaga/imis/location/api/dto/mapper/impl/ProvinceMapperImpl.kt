package np.gov.mofaga.imis.location.api.dto.mapper.impl

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.api.dto.mapper.LocationSummaryMapper
import np.gov.mofaga.imis.location.api.dto.mapper.ProvinceMapper
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.api.dto.response.ProvinceProjection
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProvinceMapperImpl(
    private val locationSummaryMapper: LocationSummaryMapper,
    private val geometryConverter: GeometryConverter,
) : ProvinceMapper {
    override fun toResponse(province: Province): ProvinceResponse {
        validateRequiredFields(province)

        return ProvinceResponse(
            code = province.code!!,
            name = province.name!!,
            nameNepali = province.nameNepali!!,
            area = province.area,
            population = province.population,
            headquarter = province.headquarter,
            headquarterNepali = province.headquarterNepali,
            districtCount = province.districts.size,
            totalPopulation = computeTotalPopulation(province),
            totalArea = computeTotalArea(province),
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
            geometry = geometryConverter.convertToGeoJson(province.geometry),
        )
    }

    override fun toSummaryResponse(province: Province): ProvinceSummaryResponse = locationSummaryMapper.toProvinceSummary(province)

    override fun toProjection(
        province: Province,
        fields: Set<ProvinceField>,
    ): ProvinceProjection {
        validateRequiredFields(province)
        return ProvinceProjectionImpl(
            code = province.code!!,
            name = province.name.takeIf { fields.contains(ProvinceField.NAME) },
            nameNepali = province.nameNepali.takeIf { fields.contains(ProvinceField.NAME_NEPALI) },
            area = province.area.takeIf { fields.contains(ProvinceField.AREA) },
            population = province.population.takeIf { fields.contains(ProvinceField.POPULATION) },
            headquarter = province.headquarter.takeIf { fields.contains(ProvinceField.HEADQUARTER) },
            headquarterNepali = province.headquarterNepali.takeIf { fields.contains(ProvinceField.HEADQUARTER_NEPALI) },
            districtCount = province.districts.size.takeIf { fields.contains(ProvinceField.DISTRICT_COUNT) },
            totalMunicipalities =
                province.districts
                    .sumOf { it.municipalities.size }
                    .takeIf { fields.contains(ProvinceField.TOTAL_MUNICIPALITIES) },
            totalPopulation =
                province.districts
                    .sumOf { it.population ?: 0L }
                    .takeIf { fields.contains(ProvinceField.TOTAL_POPULATION) },
            totalArea =
                province.districts
                    .mapNotNull { it.area }
                    .fold(BigDecimal.ZERO) { acc, area -> acc.add(area) }
                    .takeIf { fields.contains(ProvinceField.TOTAL_AREA) },
            geometry =
                geometryConverter
                    .convertToGeoJson(province.geometry)
                    .takeIf { fields.contains(ProvinceField.GEOMETRY) },
            districts =
                if (fields.contains(ProvinceField.DISTRICTS)) {
                    province.districts.map { district ->
                        DistrictSummaryResponse(
                            code = district.code!!,
                            name = district.name!!,
                            nameNepali = district.nameNepali!!,
                            municipalityCount = district.municipalities.size,
                        )
                    }
                } else {
                    null
                },
            createdAt = province.createdAt.takeIf { fields.contains(ProvinceField.CREATED_AT) },
            createdBy = province.createdBy.takeIf { fields.contains(ProvinceField.CREATED_BY) },
            updatedAt = province.updatedAt.takeIf { fields.contains(ProvinceField.UPDATED_AT) },
            updatedBy = province.updatedBy.takeIf { fields.contains(ProvinceField.UPDATED_BY) },
        )
    }

    private fun computeTotalPopulation(province: Province): Long = province.districts.sumOf { it.population ?: 0L }

    private fun computeTotalArea(province: Province): BigDecimal =
        province.districts
            .mapNotNull { it.area }
            .fold(BigDecimal.ZERO) { acc, area -> acc.add(area) }

    private fun validateRequiredFields(province: Province) {
        requireNotNull(province.code) { "Province code cannot be null" }
        requireNotNull(province.name) { "Province name cannot be null" }
        requireNotNull(province.nameNepali) { "Province Nepali name cannot be null" }
    }
}
