package np.gov.mofaga.imis.location.api.dto.enums

enum class ProvinceField {
    CODE,
    NAME,
    NAME_NEPALI,
    AREA,
    POPULATION,
    HEADQUARTER,
    HEADQUARTER_NEPALI,
    DISTRICT_COUNT,
    TOTAL_MUNICIPALITIES,
    TOTAL_POPULATION,
    TOTAL_AREA,
    GEOMETRY,
    DISTRICTS,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY,
    ;

    companion object {
        val DEFAULT_FIELDS = setOf(CODE, NAME, NAME_NEPALI, AREA, POPULATION)
        val SUMMARY_FIELDS = setOf(CODE, NAME, NAME_NEPALI)
        val DETAIL_FIELDS = values().toSet()

        fun fromString(value: String): ProvinceField = valueOf(value.uppercase())
    }
}
