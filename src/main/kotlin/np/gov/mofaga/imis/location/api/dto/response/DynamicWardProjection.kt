package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.WardField
import np.gov.mofaga.imis.location.domain.Ward
import np.gov.mofaga.imis.shared.projection.BaseEntityProjection
import np.gov.mofaga.imis.shared.util.GeometryConverter

class DynamicWardProjection private constructor() : BaseEntityProjection<Ward, WardField>() {
    companion object {
        fun from(
            ward: Ward,
            fields: Set<WardField>,
            geometryConverter: GeometryConverter,
        ): DynamicWardProjection =
            DynamicWardProjection().apply {
                populateFields(ward, fields, geometryConverter)
            }
    }

    private fun populateFields(
        ward: Ward,
        fields: Set<WardField>,
        geometryConverter: GeometryConverter,
    ) {
        fields.forEach { field ->
            val value =
                when (field) {
                    WardField.WARD_NUMBER -> ward.wardNumber
                    WardField.AREA -> ward.area
                    WardField.POPULATION -> ward.population
                    WardField.LATITUDE -> ward.latitude
                    WardField.LONGITUDE -> ward.longitude
                    WardField.OFFICE_LOCATION -> ward.officeLocation
                    WardField.OFFICE_LOCATION_NEPALI -> ward.officeLocationNepali
                    WardField.MUNICIPALITY ->
                        ward.municipality?.let { municipality ->
                            MunicipalitySummaryResponse(
                                code = municipality.code ?: "",
                                name = municipality.name ?: "",
                                nameNepali = municipality.nameNepali ?: "",
                                type = municipality.type ?: np.gov.mofaga.imis.location.domain.MunicipalityType.MUNICIPALITY,
                                totalWards = municipality.totalWards ?: 0,
                            )
                        }
                    WardField.DISTRICT ->
                        ward.municipality?.district?.let { district ->
                            DistrictSummaryResponse(
                                code = district.code ?: "",
                                name = district.name ?: "",
                                nameNepali = district.nameNepali ?: "",
                                municipalityCount = district.municipalities?.size ?: 0,
                            )
                        }
                    WardField.PROVINCE ->
                        ward.municipality?.district?.province?.let { province ->
                            ProvinceSummaryResponse(
                                code = province.code ?: "",
                                name = province.name ?: "",
                                nameNepali = province.nameNepali ?: "",
                            )
                        }
                    WardField.GEOMETRY -> ward.geometry?.let { geometryConverter.convertToGeoJson(it) }
                    WardField.CREATED_AT -> ward.createdAt
                    WardField.CREATED_BY -> ward.createdBy
                    WardField.UPDATED_AT -> ward.updatedAt
                    WardField.UPDATED_BY -> ward.updatedBy
                }
            addField(field, value)
        }
    }

    override fun populateFields(
        entity: Ward,
        fields: Set<WardField>,
    ): Unit = throw UnsupportedOperationException("Use the version with geometryConverter")
}
