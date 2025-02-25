package np.gov.mofaga.imis.shared.enums

interface FieldSupport {
    fun fieldName(): String
    fun toJsonFieldName(): String = fieldName().split("_")
        .mapIndexed { index, part -> 
            if (index == 0) part.lowercase() 
            else part.lowercase().capitalize()
        }
        .joinToString("")
}
