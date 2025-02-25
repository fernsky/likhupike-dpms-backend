package np.gov.mofaga.imis.shared.enums

interface FieldSupport : EntityField {
    override fun toJsonFieldName(): String {
        val words = fieldName().split('_')
        return buildString {
            words.forEachIndexed { index, word ->
                if (index == 0) {
                    append(word.lowercase())
                } else {
                    append(word.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
    }

    override fun toPropertyName(): String = fieldName().lowercase().replace("_", "")

    fun fieldName(): String
}
