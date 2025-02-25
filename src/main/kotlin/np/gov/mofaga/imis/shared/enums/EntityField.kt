package np.gov.mofaga.imis.shared.enums

interface EntityField {
    fun toJsonFieldName(): String

    fun toPropertyName(): String
}
