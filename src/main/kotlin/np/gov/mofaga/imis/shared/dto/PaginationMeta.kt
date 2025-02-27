package np.gov.mofaga.imis.shared.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination metadata")
data class PaginationMeta(
    @Schema(description = "Total number of items across all pages", example = "100")
    val total: Long,
    @Schema(description = "Current page number (1-based)", example = "1", minimum = "1")
    val page: Int,
    @Schema(description = "Number of items per page", example = "20", minimum = "1", maximum = "100")
    val size: Int,
    @Schema(description = "Whether there are more pages available", example = "true")
    val hasMore: Boolean,
    @Schema(description = "Whether there are previous pages", example = "false")
    val hasPrevious: Boolean,
    @Schema(description = "Total number of pages", example = "5")
    val totalPages: Int,
    @Schema(description = "Number of elements in current page", example = "20")
    val numberOfElements: Int,
    @Schema(description = "Whether this is the first page", example = "true")
    val isFirst: Boolean,
    @Schema(description = "Whether this is the last page", example = "false")
    val isLast: Boolean,
    @Schema(description = "Whether the current page is empty", example = "false")
    val isEmpty: Boolean,
) {
    companion object {
        fun from(page: Page<*>): PaginationMeta =
            PaginationMeta(
                total = page.totalElements,
                page = page.number + 1, // Converting to 1-based
                size = page.size,
                hasMore = page.hasNext(),
                hasPrevious = page.hasPrevious(),
                totalPages = page.totalPages,
                numberOfElements = page.numberOfElements,
                isFirst = page.isFirst,
                isLast = page.isLast,
                isEmpty = page.isEmpty,
            )
    }
}
