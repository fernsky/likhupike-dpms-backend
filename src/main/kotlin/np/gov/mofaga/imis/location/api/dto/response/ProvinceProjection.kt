package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.geojson.GeoJsonObject
import java.math.BigDecimal
import java.time.Instant

interface ProvinceProjection {
    val code: String
    val name: String?
    val nameNepali: String?
    val area: BigDecimal?
    val population: Long?
    val headquarter: String?
    val headquarterNepali: String?
    val totalArea: BigDecimal?
    val totalPopulation: Long?
    val totalMunicipalities: Int?
    val districtCount: Int?
    val geometry: GeoJsonObject?
    val districts: List<DistrictSummaryResponse>?
}

data class ProvinceProjectionImpl(
    override val code: String,
    override val name: String? = null,
    override val nameNepali: String? = null,
    override val area: BigDecimal? = null,
    override val population: Long? = null,
    override val headquarter: String? = null,
    override val headquarterNepali: String? = null,
    override val districtCount: Int? = null,
    override val totalMunicipalities: Int? = null,
    override val totalPopulation: Long? = null,
    override val totalArea: BigDecimal? = null,
    override val geometry: GeoJsonObject? = null,
    override val districts: List<DistrictSummaryResponse>? = null,
    val createdAt: Instant? = null,
    val createdBy: String? = null,
    val updatedAt: Instant? = null,
    val updatedBy: String? = null,
) : ProvinceProjection {
    companion object {
        fun from(
            province: Province,
            fields: Set<ProvinceField>,
            geometryConverter: GeometryConverter,
        ): ProvinceProjectionImpl =
            ProvinceProjectionImpl(
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
}
