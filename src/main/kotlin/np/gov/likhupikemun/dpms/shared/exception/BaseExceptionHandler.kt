package np.gov.likhupikemun.dpms.shared.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import jakarta.validation.ConstraintViolationException
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.ErrorDetails
import np.gov.likhupikemun.dpms.shared.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class BaseExceptionHandler {
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        ex: BaseException,
        request: WebRequest,
    ): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ex.statusCode)
            .body(ApiResponse(status = ApiResponse.Status.ERROR, error = ex.toErrorDetails()))

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val errorDetails =
            ErrorDetails(
                code = BaseException.DEFAULT_ERROR_CODE,
                message = "An unexpected error occurred",
                status = BaseException.DEFAULT_STATUS_CODE,
                details = mapOf("error" to ex.toString()),
            )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(status = ApiResponse.Status.ERROR, error = errorDetails))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val validationErrors = mutableMapOf<String, String>()

        ex.bindingResult.fieldErrors.forEach { error ->
            val fieldName = error.field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            validationErrors[fieldName] = errorMessage
        }

        ex.bindingResult.globalErrors.forEach { error ->
            val objectName = error.objectName
            val errorMessage = error.defaultMessage ?: "Invalid value"
            validationErrors[objectName] = errorMessage
        }

        val errorResponse =
            ErrorResponse(
                code = "VALIDATION_ERROR",
                message = "Validation failed for ${ex.bindingResult.objectName}",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details =
                    mapOf(
                        "validationErrors" to validationErrors,
                        "details" to "Please check the individual field errors for more details",
                    ),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val validationErrors = ex.constraintViolations.map { it.propertyPath.toString() to it.message }.toMap()
        val errorResponse =
            ErrorResponse(
                code = "VALIDATION_ERROR",
                message = "Validation failed",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("validationErrors" to validationErrors),
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
                code = "INVALID_REQUEST_FORMAT",
                message = "Request body is not readable or does not match the expected format",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = errors?.let { mapOf("validationErrors" to it) },
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
                code = "INVALID_DATA_FORMAT",
                message = "Request contains invalid data format",
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
                code = "UNRECOGNIZED_FIELD",
                message = "Request contains unrecognized fields: ${ex.propertyName}",
                statusCode = HttpStatus.BAD_REQUEST.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
