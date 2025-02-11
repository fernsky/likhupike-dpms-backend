package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class AuthExceptionHandler : BaseExceptionHandler() {
    @ExceptionHandler(
        EmailAlreadyExistsException::class,
        InvalidCredentialsException::class,
        UserNotFoundException::class,
        UserNotApprovedException::class,
        TokenExpiredException::class,
        InvalidTokenException::class,
        InvalidPasswordResetTokenException::class,
        PasswordResetLimitExceededException::class,
    )
    fun handleAuthExceptions(
        ex: BaseException,
        request: WebRequest,
    ) = when (ex) {
        is EmailAlreadyExistsException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.EMAIL_EXISTS))
        is InvalidCredentialsException -> createErrorResponse(ex)
        is UserNotFoundException -> createErrorResponse(ex)
        is UserNotApprovedException -> createErrorResponse(ex)
        is TokenExpiredException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.INVALID_TOKEN))
        is InvalidTokenException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.INVALID_TOKEN))
        is InvalidPasswordResetTokenException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.INVALID_TOKEN))
        is PasswordResetLimitExceededException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.PASSWORD_RESET_LIMIT))
        else -> createErrorResponse(ex)
    }
}
