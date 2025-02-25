package np.gov.mofaga.imis.location.api.dto.enums

import np.gov.mofaga.imis.shared.enums.FieldSupport

enum class MunicipalityField : FieldSupport {
    CODE,
    NAME,
    NAME_NEPALI,
    TYPE,
    AREA,
    POPULATION,
    LATITUDE,
    LONGITUDE,
    TOTAL_WARDS,
    DISTRICT,
    PROVINCE,
    TOTAL_POPULATION,
    TOTAL_AREA,
    GEOMETRY,
    WARDS,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY,
    ;

    override fun fieldName(): String = name

    companion object {
        val DEFAULT_FIELDS = setOf(CODE, NAME, NAME_NEPALI, TYPE, AREA, POPULATION)
        val SUMMARY_FIELDS = setOf(CODE, NAME, NAME_NEPALI, TYPE)
        val DETAIL_FIELDS = values().toSet()
        val AUDIT_FIELDS = setOf(CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)

        fun fromString(value: String): MunicipalityField = valueOf(value.uppercase())
    }
}
