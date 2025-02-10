package np.gov.likhupikemun.dpms.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response details")
data class ErrorResponse(
    @Schema(
        description = "Error code for client reference",
        example = "VALIDATION_ERROR",
    )
    val code: String,
    @Schema(
        description = "Human readable error message",
        example = "Invalid input data provided",
    )
    val message: String,
    @Schema(
        description = "Detailed error information",
        example = "{\"email\": \"must be a valid email address\"}",
    )
    val details: Map<String, Any>? = null,
    @Schema(
        description = "HTTP status code",
        example = "400",
    )
    val statusCode: Int,
    @Schema(
        description = "Stack trace (only included in development)",
        example = "com.example.Exception: Error details...",
    )
    val trace: String? = null,
)

// Utility function to create validation error response
fun createValidationError(errors: Map<String, String>): ErrorResponse =
    ErrorResponse(
        code = "VALIDATION_ERROR",
        message = "Validation failed",
        details = errors,
        statusCode = 400,
    )

// Utility function to create not found error response
fun createNotFoundError(
    resource: String,
    identifier: String,
): ErrorResponse =
    ErrorResponse(
        code = "NOT_FOUND",
        message = "$resource not found with identifier: $identifier",
        statusCode = 404,
    )

// Utility function to create forbidden error response
fun createForbiddenError(message: String): ErrorResponse =
    ErrorResponse(
        code = "FORBIDDEN",
        message = message,
        statusCode = 403,
    )

// Utility function to create conflict error response
fun createConflictError(
    message: String,
    details: Map<String, Any>? = null,
): ErrorResponse =
    ErrorResponse(
        code = "CONFLICT",
        message = message,
        details = details,
        statusCode = 409,
    )
