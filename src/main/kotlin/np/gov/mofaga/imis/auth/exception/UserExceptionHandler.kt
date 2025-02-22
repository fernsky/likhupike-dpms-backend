package np.gov.mofaga.imis.auth.exception

import np.gov.mofaga.imis.shared.exception.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class UserExceptionHandler : BaseExceptionHandler() {
    @ExceptionHandler(
        UserApprovalException::class,
        InvalidWardAssignmentException::class,
        InvalidRoleAssignmentException::class,
        UserDeactivationException::class,
        InvalidOfficePostException::class,
        WardUserCreationException::class,
        UserProfileUpdateException::class,
        ProfilePictureException::class,
        UserDeletionException::class,
    )
    fun handleUserExceptions(
        ex: BaseException,
        request: WebRequest,
    ) = when (ex) {
        is UserApprovalException -> createErrorResponse(ex) // Default error response sufficient for this case
        is WardUserCreationException -> createErrorResponse(ex) // Default error response sufficient for this case
        is InvalidWardAssignmentException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.INVALID_WARD_ASSIGNMENT))
        is InvalidRoleAssignmentException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.INVALID_ROLE_PERMISSION))
        is UserDeactivationException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.USER_DEACTIVATION))
        is InvalidOfficePostException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.INVALID_OFFICE_POST))
        is UserProfileUpdateException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.PROFILE_UPDATE))
        is ProfilePictureException -> createErrorResponse(ex, getErrorDetails(AuthErrorDetailType.PROFILE_PICTURE))
        is UserDeletionException -> createErrorResponse(ex) // Default error response sufficient for this case
        else -> createErrorResponse(ex)
    }
}
