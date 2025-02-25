package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.MunicipalityField
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.shared.projection.BaseEntityProjection
import np.gov.mofaga.imis.shared.util.GeometryConverter

class DynamicMunicipalityProjection private constructor() : BaseEntityProjection<Municipality, MunicipalityField>() {
    companion object {
        fun from(
            municipality: Municipality,
            fields: Set<MunicipalityField>,
            geometryConverter: GeometryConverter,
        ): DynamicMunicipalityProjection =
            DynamicMunicipalityProjection().apply {
                populateFields(municipality, fields, geometryConverter)
            }
    }

    private fun populateFields(
        municipality: Municipality,
        fields: Set<MunicipalityField>,
        geometryConverter: GeometryConverter,
    ) {
        fields.forEach { field ->
            val value =
                when (field) {
                    MunicipalityField.CODE -> municipality.code
                    MunicipalityField.NAME -> municipality.name
                    MunicipalityField.NAME_NEPALI -> municipality.nameNepali
                    MunicipalityField.TYPE -> municipality.type
                    MunicipalityField.AREA -> municipality.area
                    MunicipalityField.POPULATION -> municipality.population
                    MunicipalityField.LATITUDE -> municipality.latitude
                    MunicipalityField.LONGITUDE -> municipality.longitude
                    MunicipalityField.TOTAL_WARDS -> municipality.wards.size
                    MunicipalityField.DISTRICT ->
                        DistrictSummaryResponse(
                            code = municipality.district.code!!,
                            name = municipality.district.name!!,
                            nameNepali = municipality.district.nameNepali!!,
                        )
                    MunicipalityField.PROVINCE ->
                        ProvinceSummaryResponse(
                            code = municipality.district.province.code!!,
                            name = municipality.district.province.name!!,
                            nameNepali = municipality.district.province.nameNepali!!,
                        )
                    MunicipalityField.TOTAL_POPULATION -> municipality.wards.sumOf { it.population ?: 0L }
                    MunicipalityField.TOTAL_AREA ->
                        municipality.wards
                            .mapNotNull { it.area }
                            .fold(java.math.BigDecimal.ZERO) { acc, area -> acc.add(area) }
                    MunicipalityField.GEOMETRY -> municipality.geometry?.let { geometryConverter.convert(it) }
                    MunicipalityField.WARDS ->
                        municipality.wards.map { ward ->
                            WardSummaryResponse(
                                wardNumber = ward.wardNumber!!,
                                officeLocation = ward.officeLocation,
                            )
                        }
                    MunicipalityField.CREATED_AT -> municipality.createdAt
                    MunicipalityField.CREATED_BY -> municipality.createdBy
                    MunicipalityField.UPDATED_AT -> municipality.updatedAt
                    MunicipalityField.UPDATED_BY -> municipality.updatedBy
                }
            addField(field, value)
        }
    }

    override fun populateFields(
        entity: Municipality,
        fields: Set<MunicipalityField>,
    ): Unit = throw UnsupportedOperationException("Use the version with geometryConverter")
}
