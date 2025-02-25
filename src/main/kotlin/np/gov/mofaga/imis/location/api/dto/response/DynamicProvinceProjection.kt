package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.projection.BaseEntityProjection
import np.gov.mofaga.imis.shared.util.GeometryConverter

class DynamicProvinceProjection private constructor() : BaseEntityProjection<Province, ProvinceField>() {
    companion object {
        fun from(
            province: Province,
            fields: Set<ProvinceField>,
            geometryConverter: GeometryConverter,
        ): DynamicProvinceProjection =
            DynamicProvinceProjection().apply {
                populateFields(province, fields, geometryConverter)
            }
    }

    private fun populateFields(
        province: Province,
        fields: Set<ProvinceField>,
        geometryConverter: GeometryConverter,
    ) {
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
                    ProvinceField.GEOMETRY -> province.geometry?.let { geometryConverter.convert(it) }
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
            addField(field, value)
        }
    }

    override fun populateFields(
        entity: Province,
        fields: Set<ProvinceField>,
    ): Unit = throw UnsupportedOperationException("Use the version with geometryConverter")
}
