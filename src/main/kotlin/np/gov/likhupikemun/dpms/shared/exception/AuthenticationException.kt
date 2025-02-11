package np.gov.likhupikemun.dpms.shared.exception

class AuthenticationException(
    message: String,
    details: Map<String, Any>? = null,
) : BaseException(
        message = message,
        errorCode = "AUTHENTICATION_FAILED",
        statusCode = 401,
        details = details,
    )

class JwtAuthenticationException(
    message: String = "Invalid or expired JWT token",
    details: Map<String, Any>? = null,
) : AuthenticationException(message, details)

class InvalidCredentialsException(
    message: String = "Invalid credentials",
) : AuthenticationException(message)
