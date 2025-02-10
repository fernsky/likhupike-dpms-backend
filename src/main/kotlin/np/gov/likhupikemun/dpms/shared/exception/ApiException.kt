package np.gov.likhupikemun.dpms.shared.exception

sealed class ApiException(
    override val message: String,
    val errorCode: String,
    val statusCode: Int,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

class UserNotApprovedException(
    email: String,
) : ApiException("User account not yet approved: $email", "USER_NOT_APPROVED", 403)

class InvalidCredentialsException : ApiException("Invalid email or password", "INVALID_CREDENTIALS", 401)
