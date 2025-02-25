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
                        MunicipalitySummaryResponse(
                            code = ward.municipality.code!!,
                            name = ward.municipality.name!!,
                            nameNepali = ward.municipality.nameNepali!!,
                            type = ward.municipality.type!!,
                        )
                    WardField.DISTRICT ->
                        DistrictSummaryResponse(
                            code = ward.municipality.district.code!!,
                            name = ward.municipality.district.name!!,
                            nameNepali = ward.municipality.district.nameNepali!!,
                        )
                    WardField.PROVINCE ->
                        ProvinceSummaryResponse(
                            code = ward.municipality.district.province.code!!,
                            name = ward.municipality.district.province.name!!,
                            nameNepali = ward.municipality.district.province.nameNepali!!,
                        )
                    WardField.GEOMETRY -> ward.geometry?.let { geometryConverter.convert(it) }
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
