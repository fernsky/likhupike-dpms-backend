package np.gov.mofaga.imis.shared.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import np.gov.mofaga.imis.shared.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val errorResponse =
            ErrorResponse(
                message = "Authentication required",
                code = "AUTHENTICATION_REQUIRED",
                statusCode = HttpStatus.UNAUTHORIZED.value(),
                details = mapOf("error" to "Full authentication is required to access this resource"),
            )

        val mapper = ObjectMapper()
        mapper.writeValue(response.outputStream, errorResponse)
    }
}
