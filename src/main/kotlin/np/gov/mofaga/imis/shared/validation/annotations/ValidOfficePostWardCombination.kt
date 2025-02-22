package np.gov.mofaga.imis.shared.validation.annotations

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [OfficePostWardValidator::class])
annotation class ValidOfficePostWardCombination(
    val message: String = "Chief Administrative Officer cannot be assigned to a specific ward",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
