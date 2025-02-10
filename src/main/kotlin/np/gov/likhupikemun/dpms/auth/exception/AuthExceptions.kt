package np.gov.likhupikemun.dpms.auth.exception

abstract class AuthException(
    message: String,
    val errorCode: String,
    val statusCode: Int,
) : RuntimeException(message)

class EmailAlreadyExistsException(
    email: String,
) : AuthException(
        message = "User with email $email already exists",
        errorCode = "AUTH_001",
        statusCode = 409,
    )

class InvalidCredentialsException :
    AuthException(
        message = "Invalid email or password",
        errorCode = "AUTH_002",
        statusCode = 401,
    )

class UserNotFoundException(
    identifier: String,
) : AuthException(
        message = "User not found with identifier: $identifier",
        errorCode = "AUTH_003",
        statusCode = 404,
    )

class UserNotApprovedException :
    AuthException(
        message = "User account is pending approval",
        errorCode = "AUTH_004",
        statusCode = 403,
    )

class TokenExpiredException :
    AuthException(
        message = "Authentication token has expired",
        errorCode = "AUTH_005",
        statusCode = 401,
    )

class InvalidTokenException :
    AuthException(
        message = "Invalid authentication token",
        errorCode = "AUTH_006",
        statusCode = 401,
    )

class InvalidPasswordResetTokenException :
    AuthException(
        message = "Invalid or expired password reset token",
        errorCode = "AUTH_007",
        statusCode = 400,
    )

class PasswordResetLimitExceededException :
    AuthException(
        message = "Password reset request limit exceeded. Please try again later",
        errorCode = "AUTH_008",
        statusCode = 429,
    )
