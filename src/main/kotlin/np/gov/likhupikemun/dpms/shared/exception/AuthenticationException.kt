package np.gov.likhupikemun.dpms.shared.exception

open class AuthenticationException(
    message: String,
    details: Map<String, Any>? = null,
) : BaseException(
        message = message,
        errorCode = "AUTHENTICATION_FAILED",
        statusCode = 401,
        details = details,
    )

open class JwtAuthenticationException(
    message: String = "Invalid or expired JWT token",
    details: Map<String, Any>? = null,
) : AuthenticationException(message, details)
