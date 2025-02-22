package np.gov.mofaga.imis.auth.exception

import np.gov.mofaga.imis.shared.exception.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class SearchExceptionHandler : BaseExceptionHandler() {
    @ExceptionHandler(
        InvalidSearchCriteriaException::class,
        InvalidSortFieldException::class,
        InvalidDateRangeException::class,
    )
    fun handleSearchExceptions(
        ex: BaseException,
        request: WebRequest,
    ) = when (ex) {
        is InvalidSearchCriteriaException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.INVALID_SEARCH_PARAMS))
        is InvalidSortFieldException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.INVALID_SORT_FIELD))
        is InvalidDateRangeException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.INVALID_DATE_RANGE))
        else -> createErrorResponse(ex)
    }
}
