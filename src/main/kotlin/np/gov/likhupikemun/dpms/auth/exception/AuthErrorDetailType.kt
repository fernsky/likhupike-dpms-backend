package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseErrorDetailType

enum class AuthErrorDetailType(
    override val description: String,
) : BaseErrorDetailType {
    // User management and permissions
    INVALID_WARD_ASSIGNMENT("The ward assignment is invalid. Please check ward number and user permissions."),
    INVALID_ROLE_PERMISSION("You do not have permission to assign these roles or the role combination is invalid."),
    USER_DEACTIVATION("The user deactivation process failed. Please verify permissions and try again."),
    INVALID_OFFICE_POST("The provided office post is not valid for this user type or level."),
    INVALID_OFFICE_POST_WARD_COMBINATION("The provided office post and ward combination is invalid."),

    // Profile management
    PROFILE_UPDATE("Failed to update user profile. Please verify the provided information."),
    PROFILE_PICTURE("Failed to process profile picture. Please check file format and size."),

    // Authentication and security
    PASSWORD_RESET_LIMIT("You have exceeded the maximum number of password reset attempts. Please try again later."),
    EMAIL_EXISTS("An account with this email address already exists"),
    INVALID_TOKEN("The provided token is invalid or has expired"),
}
