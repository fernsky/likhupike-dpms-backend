package np.gov.mofaga.imis.shared.enums

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FieldSupportTest {
    enum class TestField : FieldSupport {
        WARD_NUMBER,
        USER_ID,
        SIMPLE_FIELD;

        override fun fieldName(): String = name
    }

    @Test
    fun `toJsonFieldName should properly convert field names to camelCase`() {
        assertEquals("wardNumber", TestField.WARD_NUMBER.toJsonFieldName())
        assertEquals("userId", TestField.USER_ID.toJsonFieldName())
        assertEquals("simpleField", TestField.SIMPLE_FIELD.toJsonFieldName())
    }
}
