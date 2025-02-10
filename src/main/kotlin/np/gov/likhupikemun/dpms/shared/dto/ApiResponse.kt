package np.gov.likhupikemun.dpms.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
data class ApiResponse<T>(
    @Schema(
        description = "Response status",
        allowableValues = ["SUCCESS", "ERROR"],
        example = "SUCCESS",
    )
    val status: Status,
    @Schema(description = "Response payload")
    val data: T? = null,
    @Schema(
        description = "Optional success message",
        example = "User created successfully",
    )
    val message: String? = null,
    @Schema(description = "Error details if status is ERROR")
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
        ) = ApiResponse(
            status = Status.SUCCESS,
            data = data,
            message = message,
        )

        fun error(error: ErrorDetails) =
            ApiResponse<Nothing>(
                status = Status.ERROR,
                error = error,
            )
    }

    enum class Status {
        SUCCESS,
        ERROR,
    }
}

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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Paginated response wrapper")
data class PagedResponse<T>(
    @Schema(description = "List of items in current page")
    val content: List<T>,
    @Schema(
        description = "Total number of items",
        example = "100",
    )
    val totalElements: Long,
    @Schema(
        description = "Total number of pages",
        example = "10",
    )
    val totalPages: Int,
    @Schema(
        description = "Current page number (0-based)",
        example = "0",
    )
    val pageNumber: Int,
    @Schema(
        description = "Number of items per page",
        example = "10",
    )
    val pageSize: Int,
    @Schema(
        description = "Whether this is the first page",
        example = "true",
    )
    val isFirst: Boolean,
    @Schema(
        description = "Whether this is the last page",
        example = "false",
    )
    val isLast: Boolean,
    @Schema(
        description = "Whether there are more pages",
        example = "true",
    )
    val hasNext: Boolean,
    @Schema(
        description = "Whether there are previous pages",
        example = "false",
    )
    val hasPrevious: Boolean,
) {
    companion object {
        fun <T> from(page: Page<T>): PagedResponse<T> =
            PagedResponse(
                content = page.content,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                pageNumber = page.number,
                pageSize = page.size,
                isFirst = page.isFirst,
                isLast = page.isLast,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious(),
            )
    }
}

// Extension function to wrap Page in ApiResponse
fun <T> Page<T>.toApiResponse(message: String? = null): ApiResponse<PagedResponse<T>> =
    ApiResponse.success(PagedResponse.from(this), message)

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
