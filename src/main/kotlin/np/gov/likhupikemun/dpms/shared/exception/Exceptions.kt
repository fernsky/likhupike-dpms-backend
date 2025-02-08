package np.gov.likhupikemun.dpms.shared.exception

class UnauthorizedException(
    message: String,
) : RuntimeException(message)

class InvalidOperationException(
    message: String,
) : RuntimeException(message)
