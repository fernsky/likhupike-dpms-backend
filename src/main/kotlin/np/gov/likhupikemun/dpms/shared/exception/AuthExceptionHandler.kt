package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExistsException(
        ex: EmailAlreadyExistsException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Email already exists",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
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
                message = ex.message ?: "User not found",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
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
                message = ex.message ?: "User not approved",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
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
                message = ex.message ?: "Invalid credentials",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
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
                message = ex.message ?: "Invalid office post",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
                details = "Please provide a valid office post from the allowed list",
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
                message = ex.message ?: "Invalid office post and ward combination",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
                details = "Chief Administrative Officer operates at municipality level and cannot be assigned to a specific ward",
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
                message = ex.message ?: "Token has expired",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
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
                message = ex.message ?: "Invalid token",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
            )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }
}
