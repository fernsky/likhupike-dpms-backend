package np.gov.likhupikemun.dpms.shared.security.jwt

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)
