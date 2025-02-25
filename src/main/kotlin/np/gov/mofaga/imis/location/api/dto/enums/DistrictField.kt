package np.gov.mofaga.imis.location.api.dto.enums

import np.gov.mofaga.imis.shared.enums.FieldSupport

enum class DistrictField : FieldSupport {
    CODE,
    NAME,
    NAME_NEPALI,
    AREA,
    POPULATION,
    HEADQUARTER,
    HEADQUARTER_NEPALI,
    PROVINCE,
    MUNICIPALITY_COUNT,
    TOTAL_POPULATION,
    TOTAL_AREA,
    GEOMETRY,
    MUNICIPALITIES,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY,
    ;

    override fun fieldName(): String = name

    companion object {
        val DEFAULT_FIELDS = setOf(CODE, NAME, NAME_NEPALI, AREA, POPULATION)
        val SUMMARY_FIELDS = setOf(CODE, NAME, NAME_NEPALI)
        val DETAIL_FIELDS = values().toSet()
        val AUDIT_FIELDS = setOf(CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)

        fun fromString(value: String): DistrictField = valueOf(value.uppercase())
    }
}
