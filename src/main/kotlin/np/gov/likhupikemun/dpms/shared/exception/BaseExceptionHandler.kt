package np.gov.likhupikemun.dpms.shared.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class BaseExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = "An unexpected error occurred",
                errorCode = "INTERNAL_SERVER_ERROR",
                statusCode = 500,
            )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errors = mutableMapOf<String, String>()

        ex.bindingResult.fieldErrors.forEach { error ->
            val fieldName = error.field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            errors[fieldName] = errorMessage
        }

        ex.bindingResult.globalErrors.forEach { error ->
            val objectName = error.objectName
            val errorMessage = error.defaultMessage ?: "Invalid value"
            errors[objectName] = errorMessage
        }

        val errorResponse =
            ErrorResponse(
                message = "Validation failed for ${ex.bindingResult.objectName}",
                errorCode = "VALIDATION_ERROR",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                errors = errors,
                details = "Please check the individual field errors for more details",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errors = ex.constraintViolations.map { it.propertyPath.toString() to it.message }.toMap()
        val errorResponse =
            ErrorResponse(
                message = "Validation failed",
                errorCode = "VALIDATION_ERROR",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                errors = errors,
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val cause = ex.cause
        val errors: Map<String, String>? =
            when (cause) {
                is InvalidFormatException ->
                    mapOf(
                        cause.path.joinToString(".") { it.fieldName ?: "unknown" } to "Invalid value: ${cause.value}",
                    )
                is UnrecognizedPropertyException ->
                    mapOf(
                        cause.path.joinToString(".") { it.fieldName ?: "unknown" } to
                            "Unrecognized field: ${cause.propertyName ?: "unknown"}",
                    )
                is MissingKotlinParameterException ->
                    mapOf(
                        (cause.parameter.name ?: "unknown") to "Missing required field: ${(cause.parameter.name ?: "unknown")}",
                    )
                else -> null
            }
        val errorResponse =
            ErrorResponse(
                message = "Request body is not readable or does not match the expected format",
                errorCode = "INVALID_REQUEST_FORMAT",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                errors = errors,
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidFormatException::class)
    fun handleInvalidFormatException(
        ex: InvalidFormatException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = "Request contains invalid data format",
                errorCode = "INVALID_DATA_FORMAT",
                statusCode = HttpStatus.BAD_REQUEST.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnrecognizedPropertyException::class)
    fun handleUnrecognizedPropertyException(
        ex: UnrecognizedPropertyException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = "Request contains unrecognized fields: ${ex.propertyName}",
                errorCode = "UNRECOGNIZED_FIELD",
                statusCode = HttpStatus.BAD_REQUEST.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
