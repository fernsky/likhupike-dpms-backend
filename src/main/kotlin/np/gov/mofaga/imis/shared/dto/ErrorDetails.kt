package np.gov.mofaga.imis.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error details when API response indicates an error")
data class ErrorDetails(
    @Schema(
        description = "Error code for client reference",
        example = "USER_NOT_FOUND",
    )
    val code: String,
    @Schema(
        description = "Human readable error message",
        example = "User with ID 123 was not found",
    )
    val message: String,
    @Schema(
        description = "Additional error details or validation errors",
        example = "{\"field\": \"email\", \"error\": \"must be a valid email address\"}",
    )
    val details: Map<String, Any>? = null,
    @Schema(
        description = "HTTP status code",
        example = "404",
    )
    val status: Int,
)
