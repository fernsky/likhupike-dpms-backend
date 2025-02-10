package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.dto.ErrorResponse
import np.gov.likhupikemun.dpms.shared.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistsException(
        ex: EmailAlreadyExistsException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "EMAIL_ALREADY_EXISTS",
                message = ex.message ?: "Email already exists",
                statusCode = HttpStatus.CONFLICT.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        ex: UserNotFoundException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "USER_NOT_FOUND",
                message = ex.message ?: "User not found",
                statusCode = HttpStatus.NOT_FOUND.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserNotApprovedException::class)
    fun handleUserNotApprovedException(
        ex: UserNotApprovedException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "USER_NOT_APPROVED",
                message = ex.message ?: "User not approved",
                statusCode = HttpStatus.FORBIDDEN.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(
        ex: InvalidCredentialsException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "INVALID_CREDENTIALS",
                message = ex.message ?: "Invalid credentials",
                statusCode = HttpStatus.UNAUTHORIZED.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(InvalidOfficePostException::class)
    fun handleInvalidOfficePostException(
        ex: InvalidOfficePostException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "INVALID_OFFICE_POST",
                message = ex.message ?: "Invalid office post",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("details" to "Please provide a valid office post from the allowed list")
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidOfficePostWardCombinationException::class)
    fun handleInvalidOfficePostWardCombinationException(
        ex: InvalidOfficePostWardCombinationException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "INVALID_OFFICE_POST_WARD_COMBINATION",
                message = ex.message ?: "Invalid office post and ward combination",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("details" to "Chief Administrative Officer operates at municipality level and cannot be assigned to a specific ward")
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(
        ex: TokenExpiredException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "TOKEN_EXPIRED",
                message = ex.message ?: "Token has expired",
                statusCode = HttpStatus.UNAUTHORIZED.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(
        ex: InvalidTokenException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "INVALID_TOKEN",
                message = ex.message ?: "Invalid token",
                statusCode = HttpStatus.UNAUTHORIZED.value(),
            )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(InvalidPasswordResetTokenException::class)
    fun handleInvalidPasswordResetTokenException(
        ex: InvalidPasswordResetTokenException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "INVALID_PASSWORD_RESET_TOKEN",
                message = ex.message ?: "Invalid or expired password reset token",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("details" to "The password reset token is invalid or has expired. Please request a new password reset.")
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(PasswordResetLimitExceededException::class)
    fun handlePasswordResetLimitExceededException(
        ex: PasswordResetLimitExceededException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = "PASSWORD_RESET_LIMIT_EXCEEDED",
                message = ex.message ?: "Password reset request limit exceeded",
                statusCode = HttpStatus.TOO_MANY_REQUESTS.value(),
                details = mapOf("details" to "You have exceeded the maximum number of password reset attempts. Please try again later.")
            )
        return ResponseEntity(errorResponse, HttpStatus.TOO_MANY_REQUESTS)
    }
}
