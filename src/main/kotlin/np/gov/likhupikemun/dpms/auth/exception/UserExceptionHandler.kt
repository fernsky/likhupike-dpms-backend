package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class UserExceptionHandler {
    @ExceptionHandler(UserApprovalException::class)
    fun handleUserApprovalException(
        ex: UserApprovalException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Error during user approval",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
                details = "The user approval process failed. Please verify the requirements and try again.",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidWardAssignmentException::class)
    fun handleInvalidWardAssignmentException(
        ex: InvalidWardAssignmentException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Invalid ward assignment",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
                details = "The ward assignment is invalid. Please check ward number and user permissions.",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidRoleAssignmentException::class)
    fun handleInvalidRoleAssignmentException(
        ex: InvalidRoleAssignmentException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Invalid role assignment",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
                details = "You do not have permission to assign these roles or the role combination is invalid.",
            )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(UserDeactivationException::class)
    fun handleUserDeactivationException(
        ex: UserDeactivationException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Error during user deactivation",
                errorCode = ex.errorCode,
                statusCode = ex.statusCode,
                details = "The user deactivation process failed. Please verify permissions and try again.",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidOfficePostException::class)
    fun handleInvalidOfficePostException(
        ex: InvalidOfficePostException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Invalid office post",
                errorCode = "INVALID_OFFICE_POST",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = "The provided office post is not valid for this user type or level.",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(WardUserCreationException::class)
    fun handleWardUserCreationException(
        ex: WardUserCreationException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Error creating ward user",
                errorCode = "WARD_USER_CREATION_ERROR",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = "Failed to create ward user. Please verify ward number and permissions.",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserProfileUpdateException::class)
    fun handleUserProfileUpdateException(
        ex: UserProfileUpdateException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Error updating user profile",
                errorCode = "PROFILE_UPDATE_ERROR",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = "Failed to update user profile. Please verify the provided information.",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ProfilePictureException::class)
    fun handleProfilePictureException(
        ex: ProfilePictureException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                message = ex.message ?: "Error processing profile picture",
                errorCode = "PROFILE_PICTURE_ERROR",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = "Failed to process profile picture. Please check file format and size.",
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
