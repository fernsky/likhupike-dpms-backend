package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.MunicipalityField
import np.gov.mofaga.imis.location.api.dto.response.DistrictSummaryResponse
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
                        municipality.district?.let { district ->
                            DistrictSummaryResponse(
                                code = district.code ?: "",
                                name = district.name ?: "",
                                nameNepali = district.nameNepali ?: "",
                                municipalityCount = district.municipalities?.size ?: 0,
                            )
                        }
                    MunicipalityField.PROVINCE ->
                        municipality.district?.province?.let { province ->
                            ProvinceSummaryResponse(
                                code = province.code ?: "",
                                name = province.name ?: "",
                                nameNepali = province.nameNepali ?: "",
                            )
                        }
                    MunicipalityField.TOTAL_POPULATION -> municipality.wards?.sumOf { it.population ?: 0L } ?: 0L
                    MunicipalityField.TOTAL_AREA ->
                        municipality.wards
                            ?.mapNotNull { it.area }
                            ?.fold(java.math.BigDecimal.ZERO) { acc, area -> acc.add(area) }
                            ?: java.math.BigDecimal.ZERO
                    MunicipalityField.GEOMETRY -> municipality.geometry?.let { geometryConverter.convertToGeoJson(it) }
                    MunicipalityField.WARDS ->
                        municipality.wards?.map { ward ->
                            WardSummaryResponse(
                                wardNumber = ward.wardNumber ?: 0,
                                population = ward.population ?: 0L,
                            )
                        } ?: emptyList<WardSummaryResponse>() // Add explicit type parameter here
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
