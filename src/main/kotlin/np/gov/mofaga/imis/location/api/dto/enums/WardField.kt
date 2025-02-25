package np.gov.mofaga.imis.location.api.dto.enums

import np.gov.mofaga.imis.shared.enums.FieldSupport

enum class WardField : FieldSupport {
    WARD_NUMBER,
    AREA,
    POPULATION,
    LATITUDE,
    LONGITUDE,
    OFFICE_LOCATION,
    OFFICE_LOCATION_NEPALI,
    MUNICIPALITY,
    DISTRICT,
    PROVINCE,
    GEOMETRY,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY,
    ;

    override fun fieldName(): String = name

    companion object {
        val DEFAULT_FIELDS = setOf(WARD_NUMBER, AREA, POPULATION, OFFICE_LOCATION)
        val SUMMARY_FIELDS = setOf(WARD_NUMBER, OFFICE_LOCATION)
        val DETAIL_FIELDS = values().toSet()
        val AUDIT_FIELDS = setOf(CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)

        fun fromString(value: String): WardField = valueOf(value.uppercase())
    }
}
