package np.gov.mofaga.imis.location.api.dto.mapper.impl

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.api.dto.mapper.LocationSummaryMapper
import np.gov.mofaga.imis.location.api.dto.mapper.ProvinceMapper
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProvinceMapperImpl(
    private val locationSummaryMapper: LocationSummaryMapper,
    private val geometryConverter: GeometryConverter,
) : ProvinceMapper {
    private val logger: Logger = LoggerFactory.getLogger(ProvinceMapperImpl::class.java)

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
    ): DynamicProvinceProjection {
        validateRequiredFields(province)
        return DynamicProvinceProjection.from(province, fields, geometryConverter)
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
