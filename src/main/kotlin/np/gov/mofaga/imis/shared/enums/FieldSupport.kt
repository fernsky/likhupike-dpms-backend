package np.gov.mofaga.imis.shared.enums

interface FieldSupport : EntityField {
    override fun toJsonFieldName(): String =
        fieldName()
            .split('_')
            .mapIndexed { index, part ->
                if (index == 0) {
                    part.lowercase()
                } else {
                    part.lowercase().capitalize()
                }
            }.joinToString("")

    override fun toPropertyName(): String = fieldName().lowercase().replace("_", "")

    fun fieldName(): String
}
