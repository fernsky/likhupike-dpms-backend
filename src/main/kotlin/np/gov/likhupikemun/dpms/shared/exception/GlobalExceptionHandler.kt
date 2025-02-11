package np.gov.likhupikemun.dpms.shared.exception

import jakarta.validation.ConstraintViolationException
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.ErrorResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler : BaseExceptionHandler() {
    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> =
        createErrorResponse(ex, ex.details?.mapValues { it.value.toString() })

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val details =
            ex.constraintViolations.associate {
                it.propertyPath.toString() to it.message
            }
        return createErrorResponse(
            InvalidInputException("Constraint violation"),
            details,
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(ex: DataIntegrityViolationException): ResponseEntity<ErrorResponse> =
        createErrorResponse(
            ConflictException("Data integrity violation"),
            getErrorDetails(SharedErrorDetailType.CONSTRAINT_VIOLATION),
        )

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponse(
                    message = "Access denied: insufficient permissions",
                    code = "AUTHORIZATION_FAILED",
                    statusCode = HttpStatus.FORBIDDEN.value(),
                    details =
                        mapOf(
                            "error" to (ex.message ?: "Insufficient permissions"),
                            "required_roles" to "Required roles information not available",
                        ),
                ),
            )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors =
            ex.bindingResult.fieldErrors.associate {
                it.field to (it.defaultMessage ?: "Invalid value")
            }
        return createErrorResponse(
            ValidationException("Validation failed", errors),
            errors,
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception): ResponseEntity<ErrorResponse> =
        createErrorResponse(
            InternalServerException(
                message = "An unexpected error occurred",
                details = mapOf("error" to (ex.message ?: "Unknown error")),
            ),
        )

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiResponse.error(
                    error = ex.toErrorDetails(),
                ),
            )

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(ex: ForbiddenException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(
                ApiResponse.error(
                    error = ex.toErrorDetails(),
                ),
            )
}
