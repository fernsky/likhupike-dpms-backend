package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.slf4j.LoggerFactory

class DynamicProvinceProjection private constructor(
    private val data: Map<String, Any?>,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(DynamicProvinceProjection::class.java)

        fun from(
            province: Province,
            fields: Set<ProvinceField>,
            geometryConverter: GeometryConverter,
        ): DynamicProvinceProjection {
            logger.debug("Creating projection for province ${province.code} with fields: $fields")

            val data = mutableMapOf<String, Any?>()

            fields.forEach { field ->
                val value =
                    when (field) {
                        ProvinceField.CODE -> province.code
                        ProvinceField.NAME -> province.name
                        ProvinceField.NAME_NEPALI -> province.nameNepali
                        ProvinceField.AREA -> province.area
                        ProvinceField.POPULATION -> province.population
                        ProvinceField.HEADQUARTER -> province.headquarter
                        ProvinceField.HEADQUARTER_NEPALI -> province.headquarterNepali
                        ProvinceField.DISTRICT_COUNT -> province.districts.size
                        ProvinceField.TOTAL_MUNICIPALITIES -> province.districts.sumOf { it.municipalities.size }
                        ProvinceField.TOTAL_POPULATION -> province.districts.sumOf { it.population ?: 0L }
                        ProvinceField.TOTAL_AREA ->
                            province.districts
                                .mapNotNull { it.area }
                                .fold(java.math.BigDecimal.ZERO) { acc, area -> acc.add(area) }
                        ProvinceField.GEOMETRY -> province.geometry?.let { geometryConverter.convertToGeoJson(it) }
                        ProvinceField.DISTRICTS ->
                            province.districts.map { district ->
                                DistrictSummaryResponse(
                                    code = district.code!!,
                                    name = district.name!!,
                                    nameNepali = district.nameNepali!!,
                                    municipalityCount = district.municipalities.size,
                                )
                            }
                        ProvinceField.CREATED_AT -> province.createdAt
                        ProvinceField.CREATED_BY -> province.createdBy
                        ProvinceField.UPDATED_AT -> province.updatedAt
                        ProvinceField.UPDATED_BY -> province.updatedBy
                    }

                // Only add non-null values and use proper field naming
                value?.let {
                    data[field.toPropertyName()] = it
                    logger.debug("Added field ${field.toPropertyName()} with value: $it")
                }
            }

            return DynamicProvinceProjection(data)
        }
    }

    fun getValue(field: ProvinceField): Any? = data[field.toPropertyName()]

    override fun toString(): String = "DynamicProvinceProjection(data=$data)"
}
