package np.gov.mofaga.imis.shared.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import np.gov.mofaga.imis.auth.domain.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class JwtService(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long,
    @Value("\${jwt.refresh-expiration}")
    private val refreshExpiration: Long,
    private val redisTemplate: RedisTemplate<String, String>, // Add this dependency
) {
    companion object {
        private const val BLACKLIST_PREFIX = "token:blacklist:"
    }

    private val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun extractUsername(token: String): String? = extractClaim(token) { it.subject }

    fun extractEmail(token: String): String = extractUsername(token) ?: throw IllegalStateException("Token has no subject claim")

    fun generateToken(userDetails: UserDetails): String = generateToken(mapOf(), userDetails)

    fun generateToken(user: User): String =
        generateToken(
            mapOf(
                "email" to (user.email ?: ""),
                "roles" to user.roles.map { it.roleType.toString() },
            ),
            user,
        )

    fun generateRefreshToken(user: User): String =
        generateToken(
            mapOf(
                "email" to (user.email ?: ""),
                "type" to "refresh",
            ),
            user,
            refreshExpiration,
        )

    fun generateToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
        expiration: Long = jwtExpiration,
    ): String =
        Jwts
            .builder()
            .claims(extraClaims)
            .subject(userDetails.username)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(expiration, ChronoUnit.HOURS)))
            .signWith(key)
            .compact()

    fun isTokenValid(
        token: String,
        userDetails: UserDetails,
    ): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun validateToken(token: String): Boolean =
        try {
            val isBlacklisted = redisTemplate.hasKey("$BLACKLIST_PREFIX$token")
            !isBlacklisted && !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }

    fun getTokenExpirationTime(): Long = jwtExpiration

    fun generateTokenPair(user: User): TokenPair {
        val accessToken = generateToken(user)
        val refreshToken = generateRefreshToken(user)
        return TokenPair(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtExpiration,
        )
    }

    fun invalidateToken(token: String) {
        val claims = extractAllClaims(token) ?: return
        val expirationTime = claims.expiration.time
        val currentTime = System.currentTimeMillis()
        val timeToLive = (expirationTime - currentTime).coerceAtLeast(0)

        redisTemplate
            .opsForValue()
            .set(
                "$BLACKLIST_PREFIX$token",
                "invalidated",
                timeToLive,
                TimeUnit.MILLISECONDS,
            )
    }

    private fun isTokenExpired(token: String): Boolean = extractExpiration(token)?.before(Date()) ?: true

    private fun extractExpiration(token: String): Date? = extractClaim(token) { it.expiration }

    private fun <T> extractClaim(
        token: String,
        claimsResolver: (Claims) -> T,
    ): T? =
        try {
            extractAllClaims(token)?.let(claimsResolver)
        } catch (e: Exception) {
            null
        }

    private fun extractAllClaims(token: String): Claims? =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }

    private fun getRoles(user: User): List<String> = user.roles.mapNotNull { it.roleType?.getAuthority() }
}
