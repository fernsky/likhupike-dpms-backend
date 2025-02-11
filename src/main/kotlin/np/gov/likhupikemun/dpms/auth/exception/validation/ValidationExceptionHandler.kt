package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class ValidationExceptionHandler : BaseExceptionHandler() {
    @ExceptionHandler(
        InvalidOfficePostWardCombinationException::class,
    )
    fun handleValidationExceptions(
        ex: BaseException,
        request: WebRequest,
    ) = when (ex) {
        is InvalidOfficePostWardCombinationException ->
            createErrorResponse(
                ex,
                getErrorDetails(AuthErrorDetailType.INVALID_OFFICE_POST_WARD_COMBINATION),
            )
        else -> createErrorResponse(ex)
    }
}
