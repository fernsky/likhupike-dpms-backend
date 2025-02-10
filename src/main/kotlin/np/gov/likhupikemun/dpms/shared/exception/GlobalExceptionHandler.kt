package np.gov.likhupikemun.dpms.shared.exception

import np.gov.likhupikemun.dpms.shared.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred",
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                details = mapOf("error" to (ex.message ?: "Unknown error")),
            ),
            HttpStatus.INTERNAL_SERVER_ERROR,
        )
}
