package np.gov.mofaga.imis.shared.validation.annotations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import np.gov.mofaga.imis.auth.domain.OfficePost
import np.gov.mofaga.imis.shared.validation.annotations.ValidOfficePost

class ValidOfficePostValidator : ConstraintValidator<ValidOfficePost, String> {
    override fun initialize(constraintAnnotation: ValidOfficePost) {}

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate("Office post cannot be null")
                .addConstraintViolation()
            return false
        }

        if (value.isBlank()) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate("Office post cannot be empty")
                .addConstraintViolation()
            return false
        }

        val isValid = OfficePost.fromTitle(value) != null

        if (!isValid) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(
                    "Invalid office post. Must be one of: ${OfficePost.getAllTitles().joinToString(", ")}",
                ).addConstraintViolation()
        }

        return isValid
    }
}
