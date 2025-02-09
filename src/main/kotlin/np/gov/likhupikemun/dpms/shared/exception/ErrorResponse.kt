package np.gov.likhupikemun.dpms.shared.exception

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class ErrorResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val message: String,
    val errorCode: String,
    val statusCode: Int,
    val path: String? = null,
    val errors: Map<String, String>? = null,
    val details: String? = null,
)
