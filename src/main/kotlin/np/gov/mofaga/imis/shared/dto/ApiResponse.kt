package np.gov.mofaga.imis.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
data class ApiResponse<T>(
    @Schema(
        description = "Indicates if the request was successful",
        example = "true",
    )
    val success: Boolean,
    @Schema(description = "Response payload")
    val data: T? = null,
    @Schema(
        description = "Response message",
        example = "User created successfully",
    )
    val message: String? = null,
    @Schema(description = "Pagination metadata (if applicable)")
    val meta: PaginationMeta? = null,
    @Schema(description = "Error details if success is false")
    val error: ErrorDetails? = null,
    @Schema(
        description = "Response timestamp",
        example = "2024-01-20T10:30:00",
    )
    val timestamp: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun <T> success(
            data: T? = null,
            message: String? = null,
            meta: PaginationMeta? = null,
        ) = ApiResponse(
            success = true,
            data = data,
            message = message,
            meta = meta,
        )

        fun <T> error(error: ErrorDetails): ApiResponse<T> =
            ApiResponse(
                success = false,
                error = error,
                data = null,
            )
    }
}

// Extension function to wrap Page in ApiResponse
fun <T> Page<T>.toApiResponse(message: String? = null): ApiResponse<List<T>> =
    ApiResponse.success(
        data = this.content,
        message = message,
        meta = PaginationMeta.from(this),
    )

// Extension function to create error response
fun errorResponse(
    code: String,
    message: String,
    details: Map<String, Any>? = null,
    status: Int,
): ApiResponse<Nothing> =
    ApiResponse.error(
        ErrorDetails(
            code = code,
            message = message,
            details = details,
            status = status,
        ),
    )
