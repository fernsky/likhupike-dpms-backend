package np.gov.mofaga.imis.shared.validation.validators

import jakarta.validation.ConstraintValidatorContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class NepaliNameValidatorTest {
    @Mock
    private lateinit var context: ConstraintValidatorContext

    private lateinit var validator: NepaliNameValidator

    @BeforeEach
    fun setup() {
        validator = NepaliNameValidator()
    }

    @Test
    fun `should return true for valid Nepali names`() {
        val validNames =
            listOf(
                "राम",
                "कृष्ण बहादुर",
                "सीता कुमारी थापा",
                "प्रकाश दाहाल",
                "रमेश",
            )

        validNames.forEach { name ->
            assertThat(validator.isValid(name, context))
                .withFailMessage("Expected '$name' to be valid")
                .isTrue()
        }
    }

    @Test
    fun `should return false for invalid Nepali names`() {
        val invalidNames =
            listOf(
                "Ram",
                "Krishna123",
                "सीता@कुमारी",
                "प्रकाश Dahal",
                "रमेश!",
            )

        invalidNames.forEach { name ->
            assertThat(validator.isValid(name, context))
                .withFailMessage("Expected '$name' to be invalid")
                .isFalse()
        }
    }

    @Test
    fun `should return false for null value`() {
        assertThat(validator.isValid(null, context)).isFalse()
    }

    @Test
    fun `should return false for empty string`() {
        assertThat(validator.isValid("", context)).isFalse()
    }

    @Test
    fun `should return false for blank string`() {
        assertThat(validator.isValid("   ", context)).isFalse()
    }

    @Test
    fun `should return true for Nepali name with spaces`() {
        assertThat(validator.isValid("राम बहादुर थापा", context)).isTrue()
    }
}
