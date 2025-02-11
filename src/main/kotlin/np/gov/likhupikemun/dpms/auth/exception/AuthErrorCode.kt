package np.gov.likhupikemun.dpms.auth.exception

import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    val code: String,
    val key: AuthErrorKey,
    val message: String,
    val statusCode: Int,
) {
    // Search related errors
    INVALID_SEARCH_CRITERIA(
        "INVALID_SEARCH_CRITERIA",
        AuthErrorKey.SEARCH_CRITERIA,
        "Invalid search criteria provided",
        HttpStatus.BAD_REQUEST.value(),
    ),
    INVALID_SORT_FIELD(
        "INVALID_SORT_FIELD",
        AuthErrorKey.SORT_FIELD,
        "Invalid sort field specified",
        HttpStatus.BAD_REQUEST.value(),
    ),
    INVALID_DATE_RANGE(
        "INVALID_DATE_RANGE",
        AuthErrorKey.DATE_RANGE,
        "Invalid date range provided",
        HttpStatus.BAD_REQUEST.value(),
    ),

    // User related errors
    USER_NOT_FOUND(
        "USER_NOT_FOUND",
        AuthErrorKey.USER_NOT_FOUND,
        "User not found",
        HttpStatus.NOT_FOUND.value(),
    ),
    USER_APPROVAL_ERROR(
        "USER_APPROVAL_ERROR",
        AuthErrorKey.USER_APPROVAL,
        "Error during user approval",
        HttpStatus.BAD_REQUEST.value(),
    ),
    INVALID_WARD_ASSIGNMENT(
        "INVALID_WARD_ASSIGNMENT",
        AuthErrorKey.WARD_ASSIGNMENT,
        "Invalid ward assignment",
        HttpStatus.BAD_REQUEST.value(),
    ),
    INVALID_ROLE_ASSIGNMENT(
        "INVALID_ROLE_ASSIGNMENT",
        AuthErrorKey.ROLE_ASSIGNMENT,
        "Invalid role assignment",
        HttpStatus.FORBIDDEN.value(),
    ),
    USER_DEACTIVATION_ERROR(
        "USER_DEACTIVATION_ERROR",
        AuthErrorKey.USER_DEACTIVATION,
        "Error during user deactivation",
        HttpStatus.BAD_REQUEST.value(),
    ),
    INVALID_OFFICE_POST(
        "INVALID_OFFICE_POST",
        AuthErrorKey.OFFICE_POST,
        "Invalid office post",
        HttpStatus.BAD_REQUEST.value(),
    ),
    WARD_USER_CREATION_ERROR(
        "WARD_USER_CREATION_ERROR",
        AuthErrorKey.WARD_USER_CREATION,
        "Error creating ward user",
        HttpStatus.BAD_REQUEST.value(),
    ),
    PROFILE_UPDATE_ERROR(
        "PROFILE_UPDATE_ERROR",
        AuthErrorKey.PROFILE_UPDATE,
        "Error updating user profile",
        HttpStatus.BAD_REQUEST.value(),
    ),
    PROFILE_PICTURE_ERROR(
        "PROFILE_PICTURE_ERROR",
        AuthErrorKey.PROFILE_PICTURE,
        "Error processing profile picture",
        HttpStatus.BAD_REQUEST.value(),
    ),
    USER_DELETION_ERROR(
        "USER_DELETION_ERROR",
        AuthErrorKey.USER_DELETION,
        "Error deleting user",
        HttpStatus.BAD_REQUEST.value(),
    ),

    // Authentication related errors
    EMAIL_ALREADY_EXISTS(
        "EMAIL_ALREADY_EXISTS",
        AuthErrorKey.EMAIL_EXISTS,
        "User with this email already exists",
        HttpStatus.CONFLICT.value(),
    ),
    INVALID_CREDENTIALS(
        "INVALID_CREDENTIALS",
        AuthErrorKey.INVALID_CREDENTIALS,
        "Invalid email or password",
        HttpStatus.UNAUTHORIZED.value(),
    ),
    USER_NOT_APPROVED(
        "USER_NOT_APPROVED",
        AuthErrorKey.USER_NOT_APPROVED,
        "User account is pending approval",
        HttpStatus.FORBIDDEN.value(),
    ),
    TOKEN_EXPIRED(
        "TOKEN_EXPIRED",
        AuthErrorKey.TOKEN_EXPIRED,
        "Authentication token has expired",
        HttpStatus.UNAUTHORIZED.value(),
    ),
    INVALID_TOKEN(
        "INVALID_TOKEN",
        AuthErrorKey.INVALID_TOKEN,
        "Invalid authentication token",
        HttpStatus.UNAUTHORIZED.value(),
    ),
    INVALID_PASSWORD_RESET_TOKEN(
        "INVALID_PASSWORD_RESET_TOKEN",
        AuthErrorKey.PASSWORD_RESET_TOKEN,
        "Invalid or expired password reset token",
        HttpStatus.BAD_REQUEST.value(),
    ),
    PASSWORD_RESET_LIMIT_EXCEEDED(
        "PASSWORD_RESET_LIMIT_EXCEEDED",
        AuthErrorKey.PASSWORD_RESET_LIMIT,
        "Password reset request limit exceeded",
        HttpStatus.TOO_MANY_REQUESTS.value(),
    ),
    INVALID_OFFICE_POST_WARD_COMBINATION(
        "INVALID_OFFICE_POST_WARD_COMBINATION",
        AuthErrorKey.INVALID_OFFICE_POST_WARD_COMBINATION,
        "Invalid office post and ward combination",
        HttpStatus.BAD_REQUEST.value(),
    ),
}
