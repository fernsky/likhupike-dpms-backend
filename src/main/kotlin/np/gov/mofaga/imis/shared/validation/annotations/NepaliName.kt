package np.gov.mofaga.imis.shared.validation.annotations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import np.gov.mofaga.imis.shared.validation.NepaliNameValidator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NepaliNameValidator::class])
annotation class NepaliName(
    val message: String = "Invalid Nepali name",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
