package np.gov.likhupikemun.dpms.shared.validation.annotations

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [GpsCoordinatesValidator::class])
annotation class ValidGpsCoordinates(
    val message: String = "Invalid GPS coordinates",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = [],
)

class GpsCoordinatesValidator : ConstraintValidator<ValidGpsCoordinates, Double?> {
    override fun isValid(
        value: Double?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value == null) return true
        return when (context.defaultConstraintMessageTemplate) {
            "Invalid latitude" -> value in -90.0..90.0
            "Invalid longitude" -> value in -180.0..180.0
            else -> true
        }
    }
}
