package np.gov.mofaga.imis.shared.validation.annotations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import np.gov.mofaga.imis.auth.domain.OfficePost
import np.gov.mofaga.imis.shared.validation.annotations.ValidOfficePost

class ValidOfficePostValidator : ConstraintValidator<ValidOfficePost, String?> {
    override fun initialize(constraintAnnotation: ValidOfficePost) {}

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        // If value is null, consider it valid (optional field)
        if (value == null) return true

        // If value is blank, consider it invalid
        if (value.isBlank()) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate("Office post cannot be blank if provided")
                .addConstraintViolation()
            return false
        }

        // Check if the value is a valid office post
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
