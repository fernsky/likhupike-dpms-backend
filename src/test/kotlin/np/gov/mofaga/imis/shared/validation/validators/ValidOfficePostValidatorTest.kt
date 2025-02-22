package np.gov.mofaga.imis.shared.validation.validators

import jakarta.validation.ConstraintValidatorContext
import np.gov.mofaga.imis.auth.domain.OfficePost
import np.gov.mofaga.imis.shared.validation.annotations.ValidOfficePostValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ValidOfficePostValidatorTest {
    @Mock
    private lateinit var context: ConstraintValidatorContext

    @Mock
    private lateinit var violationBuilder: ConstraintValidatorContext.ConstraintViolationBuilder

    private lateinit var validator: ValidOfficePostValidator

    @BeforeEach
    fun setup() {
        validator = ValidOfficePostValidator()
    }

    @Test
    fun `should return true for valid office post`() {
        OfficePost.values().forEach { post ->
            assertThat(validator.isValid(post.title, context)).isTrue()
        }
    }

    @Test
    fun `should return false for invalid office post`() {
        `when`(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder)

        val invalidPost = "Invalid Post"
        assertThat(validator.isValid(invalidPost, context)).isFalse()

        verify(context).disableDefaultConstraintViolation()
        verify(context).buildConstraintViolationWithTemplate(
            "Invalid office post. Must be one of: ${OfficePost.getAllTitles().joinToString(", ")}",
        )
    }

    @Test
    fun `should return false for null value`() {
        `when`(context.buildConstraintViolationWithTemplate("Office post cannot be null"))
            .thenReturn(violationBuilder)

        assertThat(validator.isValid(null, context)).isFalse()

        verify(context).disableDefaultConstraintViolation()
        verify(context).buildConstraintViolationWithTemplate("Office post cannot be null")
        verify(violationBuilder).addConstraintViolation()
    }

    @Test
    fun `should return false for empty string`() {
        `when`(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder)
        assertThat(validator.isValid("", context)).isFalse()
        verify(context).disableDefaultConstraintViolation()
        verify(context).buildConstraintViolationWithTemplate(anyString())
    }

    @Test
    fun `should return false for blank string`() {
        `when`(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder)
        assertThat(validator.isValid("   ", context)).isFalse()
        verify(context).disableDefaultConstraintViolation()
        verify(context).buildConstraintViolationWithTemplate(anyString())
    }
}
