package np.gov.likhupikemun.dpms.shared.config

import np.gov.likhupikemun.dpms.shared.exception.ApiException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {
    data class ErrorResponse(
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val status: Int,
        val error: String,
        val message: String,
        val code: String,
        val path: String,
    )

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                status = ex.statusCode,
                error = ex.javaClass.simpleName,
                message = ex.message,
                code = ex.errorCode,
                path = "", // TODO: Add request path
            )
        return ResponseEntity.status(ex.statusCode).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors =
            ex.bindingResult.fieldErrors.joinToString(", ") {
                "${it.field}: ${it.defaultMessage}"
            }
        val errorResponse =
            ErrorResponse(
                status = 400,
                error = "Validation Error",
                message = errors,
                code = "VALIDATION_ERROR",
                path = "", // TODO: Add request path
            )
        return ResponseEntity.badRequest().body(errorResponse)
    }
}
