package np.gov.likhupikemun.dpms.shared.validation.annotations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import np.gov.likhupikemun.dpms.auth.domain.OfficePost

class ValidOfficePostValidator : ConstraintValidator<ValidOfficePost, String> {
    override fun initialize(constraintAnnotation: ValidOfficePost) {}

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return false

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
