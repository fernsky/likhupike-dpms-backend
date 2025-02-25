package np.gov.mofaga.imis.location.api.dto.enums

import np.gov.mofaga.imis.shared.enums.EntityField

enum class MunicipalityField : EntityField {
    CODE {
        override fun toJsonFieldName() = "code"
    },
    NAME {
        override fun toJsonFieldName() = "name"
    },
    NAME_NEPALI {
        override fun toJsonFieldName() = "nameNepali"
    },
    TYPE {
        override fun toJsonFieldName() = "type"
    },
    AREA {
        override fun toJsonFieldName() = "area"
    },
    POPULATION {
        override fun toJsonFieldName() = "population"
    },
    LATITUDE {
        override fun toJsonFieldName() = "latitude"
    },
    LONGITUDE {
        override fun toJsonFieldName() = "longitude"
    },
    TOTAL_WARDS {
        override fun toJsonFieldName() = "totalWards"
    },
    DISTRICT {
        override fun toJsonFieldName() = "district"
    },
    PROVINCE {
        override fun toJsonFieldName() = "province"
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
    WARDS {
        override fun toJsonFieldName() = "wards"
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
        val DEFAULT_FIELDS = setOf(CODE, NAME, NAME_NEPALI, TYPE, TOTAL_WARDS)
        val SUMMARY_FIELDS = setOf(CODE, NAME, NAME_NEPALI, TYPE, TOTAL_WARDS)
        val DETAIL_FIELDS = values().toSet()
        val AUDIT_FIELDS = setOf(CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)
    }
}
