package np.gov.mofaga.imis.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
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

        fun <T> error(error: ErrorDetails): ApiResponse<T> =
            ApiResponse(
                status = Status.ERROR,
                error = error,
                data = null,
            )
    }

    enum class Status {
        SUCCESS,
        ERROR,
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
