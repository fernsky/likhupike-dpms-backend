package np.gov.mofaga.imis.location.api.dto.enums

import np.gov.mofaga.imis.shared.enums.FieldSupport

enum class WardField : FieldSupport {
    WARD_NUMBER {
        override fun toJsonFieldName() = "wardNumber"
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
    OFFICE_LOCATION {
        override fun toJsonFieldName() = "officeLocation"
    },
    OFFICE_LOCATION_NEPALI {
        override fun toJsonFieldName() = "officeLocationNepali"
    },
    MUNICIPALITY {
        override fun toJsonFieldName() = "municipality"
    },
    DISTRICT {
        override fun toJsonFieldName() = "district"
    },
    PROVINCE {
        override fun toJsonFieldName() = "province"
    },
    GEOMETRY {
        override fun toJsonFieldName() = "geometry"
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
        val DEFAULT_FIELDS = setOf(WARD_NUMBER, MUNICIPALITY, AREA, POPULATION, OFFICE_LOCATION)
        val SUMMARY_FIELDS = setOf(WARD_NUMBER, MUNICIPALITY)
        val DETAIL_FIELDS = values().toSet()
        val AUDIT_FIELDS = setOf(CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY)

        fun fromString(value: String): WardField = valueOf(value.uppercase())
    }
}
