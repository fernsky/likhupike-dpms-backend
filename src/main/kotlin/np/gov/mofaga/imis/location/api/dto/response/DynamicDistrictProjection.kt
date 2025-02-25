package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.DistrictField
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.shared.projection.BaseEntityProjection
import np.gov.mofaga.imis.shared.util.GeometryConverter

class DynamicDistrictProjection private constructor() : BaseEntityProjection<District, DistrictField>() {
    companion object {
        fun from(
            district: District,
            fields: Set<DistrictField>,
            geometryConverter: GeometryConverter,
        ): DynamicDistrictProjection =
            DynamicDistrictProjection().apply {
                populateFields(district, fields, geometryConverter)
            }
    }

    private fun populateFields(
        district: District,
        fields: Set<DistrictField>,
        geometryConverter: GeometryConverter,
    ) {
        fields.forEach { field ->
            val value = when (field) {
                DistrictField.CODE -> district.code
                DistrictField.NAME -> district.name
                DistrictField.NAME_NEPALI -> district.nameNepali
                DistrictField.AREA -> district.area
                DistrictField.POPULATION -> district.population
                DistrictField.HEADQUARTER -> district.headquarter
                DistrictField.HEADQUARTER_NEPALI -> district.headquarterNepali
                DistrictField.PROVINCE -> ProvinceSummaryResponse(
                    code = district.province.code!!,
                    name = district.province.name!!,
                    nameNepali = district.province.nameNepali!!
                )
                DistrictField.MUNICIPALITY_COUNT -> district.municipalities.size
                DistrictField.TOTAL_POPULATION -> district.municipalities.sumOf { it.population ?: 0L }
                DistrictField.TOTAL_AREA -> district.municipalities
                    .mapNotNull { it.area }
                    .fold(java.math.BigDecimal.ZERO) { acc, area -> acc.add(area) }
                DistrictField.GEOMETRY -> district.geometry?.let { geometryConverter.convert(it) }
                DistrictField.MUNICIPALITIES -> district.municipalities.map { municipality ->
                    MunicipalitySummaryResponse(
                        code = municipality.code!!,
                        name = municipality.name!!,
                        nameNepali = municipality.nameNepali!!,
                        type = municipality.type!!
                    )
                }
                DistrictField.CREATED_AT -> district.createdAt
                DistrictField.CREATED_BY -> district.createdBy
                DistrictField.UPDATED_AT -> district.updatedAt
                DistrictField.UPDATED_BY -> district.updatedBy
            }
            addField(field, value)
        }
    }

    override fun populateFields(
        entity: District,
        fields: Set<DistrictField>
    ): Unit = throw UnsupportedOperationException("Use the version with geometryConverter")
}
