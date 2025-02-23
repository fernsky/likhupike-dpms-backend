package np.gov.mofaga.imis.shared.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [CoordinateValidator::class])
annotation class ValidCoordinate(
    val message: String = "Invalid coordinate format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = [],
)

class CoordinateValidator : ConstraintValidator<ValidCoordinate, Array<Double>> {
    override fun initialize(constraintAnnotation: ValidCoordinate) {}

    override fun isValid(
        coordinate: Array<Double>?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (coordinate == null || coordinate.size != 2) return false
        val (lon, lat) = coordinate
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [CoordinateListValidator::class])
annotation class ValidCoordinateList(
    val message: String = "Invalid coordinate list format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = [],
)

class CoordinateListValidator : ConstraintValidator<ValidCoordinateList, List<*>> {
    override fun initialize(constraintAnnotation: ValidCoordinateList) {}

    override fun isValid(
        value: List<*>?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (value == null) return true
        return value.all { item ->
            when (item) {
                is Array<*> -> isValidCoordinate(item)
                is List<*> ->
                    item.all { subItem ->
                        when (subItem) {
                            is Array<*> -> isValidCoordinate(subItem)
                            is List<*> -> subItem.all { it is Array<*> && isValidCoordinate(it as Array<*>) }
                            else -> false
                        }
                    }
                else -> false
            }
        }
    }

    private fun isValidCoordinate(coord: Array<*>): Boolean {
        if (coord.size != 2) return false
        val (lon, lat) = coord.map { it as? Double ?: return false }
        return lat in -90.0..90.0 && lon in -180.0..180.0
    }
}
