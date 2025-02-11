package np.gov.likhupikemun.dpms.shared.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import np.gov.likhupikemun.dpms.shared.dto.ErrorResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val errorResponse =
            ErrorResponse(
                message = "Authentication required",
                code = "AUTHENTICATION_REQUIRED",
                statusCode = HttpServletResponse.SC_UNAUTHORIZED,
                details =
                    mapOf(
                        "path" to request.requestURI,
                        "error" to (authException.message ?: "Authentication is required to access this resource"),
                    ),
            )

        objectMapper.writeValue(response.outputStream, errorResponse)
    }
}
