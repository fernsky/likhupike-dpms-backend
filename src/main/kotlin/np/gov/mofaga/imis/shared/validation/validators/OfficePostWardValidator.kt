package np.gov.mofaga.imis.shared.validation.annotations

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import np.gov.mofaga.imis.auth.api.dto.RegisterRequest
import np.gov.mofaga.imis.auth.domain.OfficePost

class OfficePostWardValidator : ConstraintValidator<ValidOfficePostWardCombination, RegisterRequest> {
    override fun isValid(
        request: RegisterRequest?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (request == null) return true

        val officePost = OfficePost.fromTitle(request.officePost)

        if (officePost == OfficePost.CHIEF_ADMINISTRATIVE_OFFICER && request.wardNumber != null) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(
                    "Chief Administrative Officer cannot be assigned to a specific ward",
                ).addPropertyNode("wardNumber")
                .addConstraintViolation()
            return false
        }

        return true
    }
}
