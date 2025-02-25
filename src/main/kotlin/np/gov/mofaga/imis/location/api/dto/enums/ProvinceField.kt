package np.gov.mofaga.imis.location.api.dto.enums

import np.gov.mofaga.imis.shared.enums.EntityField

enum class ProvinceField : EntityField {
    CODE {
        override fun toJsonFieldName() = "code"
    },
    NAME {
        override fun toJsonFieldName() = "name"
    },
    NAME_NEPALI {
        override fun toJsonFieldName() = "nameNepali"
    },
    AREA {
        override fun toJsonFieldName() = "area"
    },
    POPULATION {
        override fun toJsonFieldName() = "population"
    },
    HEADQUARTER {
        override fun toJsonFieldName() = "headquarter"
    },
    HEADQUARTER_NEPALI {
        override fun toJsonFieldName() = "headquarterNepali"
    },
    DISTRICT_COUNT {
        override fun toJsonFieldName() = "districtCount"
    },
    TOTAL_MUNICIPALITIES {
        override fun toJsonFieldName() = "totalMunicipalities"
    },
    TOTAL_POPULATION {
        override fun toJsonFieldName() = "totalPopulation"
    },
    TOTAL_AREA {
        override fun toJsonFieldName() = "totalArea"
    },
    GEOMETRY {
        override fun toJsonFieldName() = "geometry"
    },
    DISTRICTS {
        override fun toJsonFieldName() = "districts"
    },
    CREATED_AT {
        override fun toJsonFieldName() = "createdAt"
    },
    CREATED_BY {
        override fun toJsonFieldName() = "createdBy"
    },
    UPDATED_AT {
        override fun toJsonFieldName() = "updatedAt"
    },
    UPDATED_BY {
        override fun toJsonFieldName() = "updatedBy"
    }, ;

    override fun fieldName(): String = name

    companion object {
        val DEFAULT_FIELDS = setOf(CODE, NAME, NAME_NEPALI, DISTRICT_COUNT)
        val SUMMARY_FIELDS = setOf(CODE, NAME, NAME_NEPALI)
        val DETAIL_FIELDS = values().toSet()
        val AUDIT_FIELDS = setOf(CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
    }
}
