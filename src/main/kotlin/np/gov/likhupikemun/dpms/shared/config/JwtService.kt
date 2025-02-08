package np.gov.likhupikemun.dpms.shared.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.context.annotation.Configuration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Configuration
class JwtService(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,
    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long,
) {
    private val key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun extractUsername(token: String): String? =
        extractClaim(token) { it.subject }

    fun generateToken(userDetails: UserDetails): String =
        generateToken(mapOf(), userDetails)

    fun generateToken(
        extraClaims: Map<String, Any>,
        userDetails: UserDetails,
    ): String =
        Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.username)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.HOURS)))
            .signWith(key)
            .compact()

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean =
        extractExpiration(token)?.before(Date()) ?: true

    private fun extractExpiration(token: String): Date? =
        extractClaim(token) { it.expiration }

    private fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T? =
        try {
            extractAllClaims(token)?.let(claimsResolver)
        } catch (e: Exception) {
            null
        }

    private fun extractAllClaims(token: String): Claims? =
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }
}
