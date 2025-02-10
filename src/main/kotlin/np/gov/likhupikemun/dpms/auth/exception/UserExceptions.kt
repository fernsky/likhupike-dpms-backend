package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseException

class UserApprovalException(
    message: String,
) : BaseException(message, "USER_APPROVAL_ERROR", 400)

class InvalidWardAssignmentException(
    message: String,
) : BaseException(message, "INVALID_WARD_ASSIGNMENT", 400)

class InvalidRoleAssignmentException(
    message: String,
) : BaseException(message, "INVALID_ROLE_ASSIGNMENT", 403)

class UserDeactivationException(
    message: String,
) : BaseException(message, "USER_DEACTIVATION_ERROR", 400)

class WardUserCreationException(
    message: String,
) : BaseException(message, "WARD_USER_CREATION_ERROR", 400)

class UserProfileUpdateException(
    message: String,
) : BaseException(message, "PROFILE_UPDATE_ERROR", 400)

class ProfilePictureException(
    message: String,
) : BaseException(message, "PROFILE_PICTURE_ERROR", 400)

class InvalidOfficePostException(
    message: String,
) : BaseException(message, "INVALID_OFFICE_POST", 400)

class UserDeletionException(
    message: String,
) : BaseException(message, "USER_DELETION_ERROR", 400)
