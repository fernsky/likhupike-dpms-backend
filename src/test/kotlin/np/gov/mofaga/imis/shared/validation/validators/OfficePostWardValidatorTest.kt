package np.gov.mofaga.imis.shared.validation.validators

import jakarta.validation.ConstraintValidatorContext
import np.gov.mofaga.imis.auth.api.dto.RegisterRequest
import np.gov.mofaga.imis.auth.domain.OfficePost
import np.gov.mofaga.imis.shared.validation.annotations.OfficePostWardValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class OfficePostWardValidatorTest {
    @Mock
    private lateinit var context: ConstraintValidatorContext

    @Mock
    private lateinit var violationBuilder: ConstraintValidatorContext.ConstraintViolationBuilder

    @Mock
    private lateinit var nodeBuilder: ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext

    private lateinit var validator: OfficePostWardValidator

    @BeforeEach
    fun setup() {
        validator = OfficePostWardValidator()
    }

    private fun createRegisterRequest(
        officePost: String,
        wardNumber: Int? = null,
    ) = RegisterRequest(
        email = "test@example.com",
        password = "Password123#",
        fullName = "Test User",
        fullNameNepali = "टेस्ट युजर",
        dateOfBirth = LocalDate.of(1990, 1, 1),
        address = "Test Address",
        officePost = officePost,
        wardNumber = wardNumber,
    )

    @Test
    fun `should return true when request is null`() {
        assertThat(validator.isValid(null, context)).isTrue()
    }

    @Test
    fun `should return false when CAO is assigned to a ward`() {
        `when`(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder)
        `when`(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder)

        val request =
            createRegisterRequest(
                officePost = OfficePost.CHIEF_ADMINISTRATIVE_OFFICER.title,
                wardNumber = 1,
            )

        assertThat(validator.isValid(request, context)).isFalse()

        verify(context).disableDefaultConstraintViolation()
        verify(violationBuilder).addPropertyNode("wardNumber")
    }

    @Test
    fun `should return true when CAO has no ward assigned`() {
        val request =
            createRegisterRequest(
                officePost = OfficePost.CHIEF_ADMINISTRATIVE_OFFICER.title,
                wardNumber = null,
            )

        assertThat(validator.isValid(request, context)).isTrue()
    }

    @Test
    fun `should return true for non-CAO posts with ward number`() {
        val nonCaoPosts =
            OfficePost
                .values()
                .filter { it != OfficePost.CHIEF_ADMINISTRATIVE_OFFICER }

        nonCaoPosts.forEach { post ->
            val request =
                createRegisterRequest(
                    officePost = post.title,
                    wardNumber = 1,
                )
            assertThat(validator.isValid(request, context)).isTrue()
        }
    }

    @Test
    fun `should return true for non-CAO posts without ward number`() {
        val nonCaoPosts =
            OfficePost
                .values()
                .filter { it != OfficePost.CHIEF_ADMINISTRATIVE_OFFICER }

        nonCaoPosts.forEach { post ->
            val request =
                createRegisterRequest(
                    officePost = post.title,
                    wardNumber = null,
                )
            assertThat(validator.isValid(request, context)).isTrue()
        }
    }
}
