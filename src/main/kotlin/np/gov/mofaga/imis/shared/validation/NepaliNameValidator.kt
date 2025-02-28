package np.gov.mofaga.imis.shared.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import np.gov.mofaga.imis.shared.validation.annotations.NepaliName

class NepaliNameValidator : ConstraintValidator<NepaliName, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return true
        val nepaliPattern = Regex("^[\\u0900-\\u097F\\s]+\$")
        return nepaliPattern.matches(value)
    }
}
