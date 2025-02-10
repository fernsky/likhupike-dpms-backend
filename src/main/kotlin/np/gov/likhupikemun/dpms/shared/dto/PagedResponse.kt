package np.gov.likhupikemun.dpms.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Paginated response wrapper")
data class PagedResponse<T>(
    @Schema(description = "List of items in current page")
    val content: List<T>,
    @Schema(description = "Total number of items across all pages", example = "100")
    val totalElements: Long,
    @Schema(description = "Total number of pages", example = "5")
    val totalPages: Int,
    @Schema(
        description = "Current page number (0-based)",
        example = "0",
        minimum = "0",
    )
    val pageNumber: Int,
    @Schema(
        description = "Number of items per page",
        example = "20",
        minimum = "1",
        maximum = "100",
    )
    val pageSize: Int,
    @Schema(description = "Whether this is the first page", example = "true")
    val isFirst: Boolean,
    @Schema(description = "Whether this is the last page", example = "false")
    val isLast: Boolean,
    @Schema(description = "Whether there are more pages", example = "true")
    val hasNext: Boolean,
    @Schema(description = "Whether there are previous pages", example = "false")
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
