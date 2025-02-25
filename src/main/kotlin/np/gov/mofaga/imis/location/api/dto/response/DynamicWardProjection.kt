package np.gov.mofaga.imis.location.api.dto.response

import np.gov.mofaga.imis.location.api.dto.enums.WardField
import np.gov.mofaga.imis.location.domain.Ward
import np.gov.mofaga.imis.shared.projection.BaseEntityProjection
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.slf4j.LoggerFactory

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
        val logger = LoggerFactory.getLogger(this::class.java)

        fields.forEach { field ->
            val value: Any? =
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
                                code = municipality.code.orEmpty(),
                                name = municipality.name.orEmpty(),
                                nameNepali = municipality.nameNepali.orEmpty(),
                                type = municipality.type ?: np.gov.mofaga.imis.location.domain.MunicipalityType.MUNICIPALITY,
                                totalWards = municipality.totalWards ?: 0,
                            )
                        }
                    WardField.DISTRICT ->
                        ward.municipality?.district?.let { district ->
                            DistrictSummaryResponse(
                                code = district.code.orEmpty(),
                                name = district.name.orEmpty(),
                                nameNepali = district.nameNepali.orEmpty(),
                                municipalityCount = district.municipalities.size,
                            )
                        }
                    WardField.PROVINCE ->
                        ward.municipality?.district?.province?.let { province ->
                            ProvinceSummaryResponse(
                                code = province.code.orEmpty(),
                                name = province.name.orEmpty(),
                                nameNepali = province.nameNepali.orEmpty(),
                            )
                        }
                    WardField.GEOMETRY -> ward.geometry?.let { geometryConverter.convertToGeoJson(it) }
                    WardField.CREATED_AT -> ward.createdAt
                    WardField.CREATED_BY -> ward.createdBy
                    WardField.UPDATED_AT -> ward.updatedAt
                    WardField.UPDATED_BY -> ward.updatedBy
                }
            logger.debug("Adding field: ${field.name}, JSON name: ${field.toJsonFieldName()}, value: $value")
            addField(field, value)
        }
    }

    override fun populateFields(
        entity: Ward,
        fields: Set<WardField>,
    ): Unit = throw UnsupportedOperationException("Use the version with geometryConverter")
}
