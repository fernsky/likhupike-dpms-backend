package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.slf4j.LoggerFactory
import java.math.BigDecimal

interface ProvinceProjection {
    val code: String // This is always required
}

class DynamicProvinceProjection private constructor(
    private val data: Map<String, Any?>,
) : ProvinceProjection {
    override val code: String
        get() = data["code"] as String

    fun getValue(field: ProvinceField): Any? = data[field.name.lowercase()]

    companion object {
        private val logger = LoggerFactory.getLogger(DynamicProvinceProjection::class.java)

        fun from(
            province: Province,
            fields: Set<ProvinceField>,
            geometryConverter: GeometryConverter,
        ): DynamicProvinceProjection {
            logger.debug("Creating projection with fields: $fields")

            val data = mutableMapOf<String, Any?>()

            // Code is always included
            data["code"] = province.code

            // Only add fields that are requested
            fields.forEach { field ->
                when (field) {
                    ProvinceField.NAME -> province.name?.let { data["name"] = it }
                    ProvinceField.NAME_NEPALI -> province.nameNepali?.let { data["nameNepali"] = it }
                    ProvinceField.AREA -> province.area?.let { data["area"] = it }
                    ProvinceField.POPULATION -> province.population?.let { data["population"] = it }
                    ProvinceField.HEADQUARTER -> province.headquarter?.let { data["headquarter"] = it }
                    ProvinceField.HEADQUARTER_NEPALI -> province.headquarterNepali?.let { data["headquarterNepali"] = it }
                    ProvinceField.DISTRICT_COUNT -> data["districtCount"] = province.districts.size
                    ProvinceField.TOTAL_MUNICIPALITIES -> data["totalMunicipalities"] = province.districts.sumOf { it.municipalities.size }
                    ProvinceField.TOTAL_POPULATION -> data["totalPopulation"] = province.districts.sumOf { it.population ?: 0L }
                    ProvinceField.TOTAL_AREA ->
                        data["totalArea"] =
                            province.districts
                                .mapNotNull { it.area }
                                .fold(BigDecimal.ZERO) { acc, area -> acc.add(area) }
                    ProvinceField.GEOMETRY ->
                        province.geometry?.let {
                            data["geometry"] = geometryConverter.convertToGeoJson(it)
                        }
                    ProvinceField.DISTRICTS ->
                        data["districts"] =
                            province.districts.map { district ->
                                DistrictSummaryResponse(
                                    code = district.code!!,
                                    name = district.name!!,
                                    nameNepali = district.nameNepali!!,
                                    municipalityCount = district.municipalities.size,
                                )
                            }
                    ProvinceField.CREATED_AT -> province.createdAt?.let { data["createdAt"] = it }
                    ProvinceField.CREATED_BY -> province.createdBy?.let { data["createdBy"] = it }
                    ProvinceField.UPDATED_AT -> province.updatedAt?.let { data["updatedAt"] = it }
                    ProvinceField.UPDATED_BY -> province.updatedBy?.let { data["updatedBy"] = it }
                    ProvinceField.CODE -> {} // Already handled
                }
            }

            logger.debug("Created projection with data: $data")
            return DynamicProvinceProjection(data)
        }
    }

    override fun toString(): String = "DynamicProvinceProjection(data=$data)"
}
