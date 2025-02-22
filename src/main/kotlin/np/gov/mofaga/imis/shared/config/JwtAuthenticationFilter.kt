package np.gov.mofaga.imis.shared.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import np.gov.mofaga.imis.shared.exception.AuthenticationException
import np.gov.mofaga.imis.shared.exception.JwtAuthenticationException
import np.gov.mofaga.imis.shared.security.jwt.JwtService
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val authHeader = request.getHeader("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            val jwt = authHeader.substring(7)

            try {
                processJwtAuthentication(jwt, request)
            } catch (e: Exception) {
                logger.error("JWT Authentication failed", e)
                throw JwtAuthenticationException(
                    details =
                        mapOf(
                            "token" to jwt,
                            "error" to (e.message ?: "Unknown error"),
                        ),
                )
            }
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun processJwtAuthentication(
        jwt: String,
        request: HttpServletRequest,
    ) {
        val userEmail = jwtService.extractUsername(jwt)

        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(userEmail)
            if (jwtService.isTokenValid(jwt, userDetails)) {
                val authToken =
                    UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities,
                    )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            } else {
                throw JwtAuthenticationException("Invalid JWT token")
            }
        }
    }
}
