package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseException

class EmailAlreadyExistsException(
    email: String,
) : BaseException(
        "User with email $email already exists",
        AuthErrorCode.EMAIL_ALREADY_EXISTS.code,
        AuthErrorCode.EMAIL_ALREADY_EXISTS.statusCode,
    )

class InvalidCredentialsException :
    BaseException(
        AuthErrorCode.INVALID_CREDENTIALS.message,
        AuthErrorCode.INVALID_CREDENTIALS.code,
        AuthErrorCode.INVALID_CREDENTIALS.statusCode,
    )

class UserNotFoundException(
    identifier: String,
) : BaseException(
        "User not found with identifier: $identifier",
        AuthErrorCode.USER_NOT_FOUND.code,
        AuthErrorCode.USER_NOT_FOUND.statusCode,
    )

class UserNotApprovedException :
    BaseException(
        AuthErrorCode.USER_NOT_APPROVED.message,
        AuthErrorCode.USER_NOT_APPROVED.code,
        AuthErrorCode.USER_NOT_APPROVED.statusCode,
    )

class TokenExpiredException :
    BaseException(
        AuthErrorCode.TOKEN_EXPIRED.message,
        AuthErrorCode.TOKEN_EXPIRED.code,
        AuthErrorCode.TOKEN_EXPIRED.statusCode,
    )

class InvalidTokenException :
    BaseException(
        AuthErrorCode.INVALID_TOKEN.message,
        AuthErrorCode.INVALID_TOKEN.code,
        AuthErrorCode.INVALID_TOKEN.statusCode,
    )

class InvalidPasswordResetTokenException :
    BaseException(
        AuthErrorCode.INVALID_PASSWORD_RESET_TOKEN.message,
        AuthErrorCode.INVALID_PASSWORD_RESET_TOKEN.code,
        AuthErrorCode.INVALID_PASSWORD_RESET_TOKEN.statusCode,
    )

class PasswordResetLimitExceededException :
    BaseException(
        AuthErrorCode.PASSWORD_RESET_LIMIT_EXCEEDED.message,
        AuthErrorCode.PASSWORD_RESET_LIMIT_EXCEEDED.code,
        AuthErrorCode.PASSWORD_RESET_LIMIT_EXCEEDED.statusCode,
    )
