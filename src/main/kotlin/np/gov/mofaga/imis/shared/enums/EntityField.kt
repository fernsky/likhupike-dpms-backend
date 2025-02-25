package np.gov.mofaga.imis.shared.enums

interface EntityField {
    fun fieldName(): String

    fun toJsonFieldName(): String

    fun toPropertyName(): String = toJsonFieldName() // Add this method
}
