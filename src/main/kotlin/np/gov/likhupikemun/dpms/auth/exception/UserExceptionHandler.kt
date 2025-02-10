package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class UserExceptionHandler {
    @ExceptionHandler(UserApprovalException::class)
    fun handleUserApprovalException(
        ex: UserApprovalException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = ex.errorCode,
                message = ex.message ?: "Error during user approval",
                statusCode = ex.statusCode,
                details = mapOf("details" to "The user approval process failed. Please verify the requirements and try again."),
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
                code = ex.errorCode,
                message = ex.message ?: "Invalid ward assignment",
                statusCode = ex.statusCode,
                details = mapOf("details" to "The ward assignment is invalid. Please check ward number and user permissions."),
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
                code = ex.errorCode,
                message = ex.message ?: "Invalid role assignment",
                statusCode = ex.statusCode,
                details = mapOf("details" to "You do not have permission to assign these roles or the role combination is invalid."),
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
                code = ex.errorCode,
                message = ex.message ?: "Error during user deactivation",
                statusCode = ex.statusCode,
                details = mapOf("details" to "The user deactivation process failed. Please verify permissions and try again."),
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
                code = "INVALID_OFFICE_POST",
                message = ex.message ?: "Invalid office post",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("details" to "The provided office post is not valid for this user type or level."),
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
                code = "WARD_USER_CREATION_ERROR",
                message = ex.message ?: "Error creating ward user",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("details" to "Failed to create ward user. Please verify ward number and permissions."),
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
                code = "PROFILE_UPDATE_ERROR",
                message = ex.message ?: "Error updating user profile",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("details" to "Failed to update user profile. Please verify the provided information."),
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
                code = "PROFILE_PICTURE_ERROR",
                message = ex.message ?: "Error processing profile picture",
                statusCode = HttpStatus.BAD_REQUEST.value(),
                details = mapOf("details" to "Failed to process profile picture. Please check file format and size."),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        ex: UserNotFoundException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity(
            ErrorResponse(
                code = "USER_NOT_FOUND",
                message = ex.message ?: "User not found", // Add null coalescing operator
                statusCode = HttpStatus.NOT_FOUND.value(),
                details = mapOf("details" to "User not found."),
            ),
            HttpStatus.NOT_FOUND,
        )

    @ExceptionHandler(UserDeletionException::class)
    fun handleUserDeletionException(
        ex: UserDeletionException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = ex.errorCode,
                message = ex.message ?: "Error deleting user",
                statusCode = ex.statusCode,
                details = mapOf("details" to "The user deletion process failed. Please verify permissions and try again."),
            )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
