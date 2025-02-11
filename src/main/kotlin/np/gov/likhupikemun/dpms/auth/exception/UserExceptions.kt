package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseException

class UserApprovalException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.USER_APPROVAL_ERROR.message,
        AuthErrorCode.USER_APPROVAL_ERROR.code,
        AuthErrorCode.USER_APPROVAL_ERROR.statusCode,
    )

class InvalidWardAssignmentException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.INVALID_WARD_ASSIGNMENT.message,
        AuthErrorCode.INVALID_WARD_ASSIGNMENT.code,
        AuthErrorCode.INVALID_WARD_ASSIGNMENT.statusCode,
    )

class InvalidRoleAssignmentException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.INVALID_ROLE_ASSIGNMENT.message,
        AuthErrorCode.INVALID_ROLE_ASSIGNMENT.code,
        AuthErrorCode.INVALID_ROLE_ASSIGNMENT.statusCode,
    )

class UserDeactivationException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.USER_DEACTIVATION_ERROR.message,
        AuthErrorCode.USER_DEACTIVATION_ERROR.code,
        AuthErrorCode.USER_DEACTIVATION_ERROR.statusCode,
    )

class InvalidOfficePostException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.INVALID_OFFICE_POST.message,
        AuthErrorCode.INVALID_OFFICE_POST.code,
        AuthErrorCode.INVALID_OFFICE_POST.statusCode,
    )

class WardUserCreationException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.WARD_USER_CREATION_ERROR.message,
        AuthErrorCode.WARD_USER_CREATION_ERROR.code,
        AuthErrorCode.WARD_USER_CREATION_ERROR.statusCode,
    )

class UserProfileUpdateException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.PROFILE_UPDATE_ERROR.message,
        AuthErrorCode.PROFILE_UPDATE_ERROR.code,
        AuthErrorCode.PROFILE_UPDATE_ERROR.statusCode,
    )

class ProfilePictureException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.PROFILE_PICTURE_ERROR.message,
        AuthErrorCode.PROFILE_PICTURE_ERROR.code,
        AuthErrorCode.PROFILE_PICTURE_ERROR.statusCode,
    )

class UserDeletionException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.USER_DELETION_ERROR.message,
        AuthErrorCode.USER_DELETION_ERROR.code,
        AuthErrorCode.USER_DELETION_ERROR.statusCode,
    )
