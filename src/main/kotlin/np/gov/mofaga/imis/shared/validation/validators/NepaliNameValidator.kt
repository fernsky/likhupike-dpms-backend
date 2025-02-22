package np.gov.mofaga.imis.shared.validation.validators

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import np.gov.mofaga.imis.shared.validation.annotations.NepaliName

class NepaliNameValidator : ConstraintValidator<NepaliName, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.isNullOrBlank()) return false
        return value.matches(Regex("^[\\u0900-\\u097F\\s]+$")) // Devanagari Unicode range
    }
}
