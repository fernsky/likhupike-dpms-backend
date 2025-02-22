package np.gov.mofaga.imis.shared.exception

import np.gov.mofaga.imis.shared.dto.ApiResponse
import np.gov.mofaga.imis.shared.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

abstract class BaseExceptionHandler {
    protected fun createErrorResponse(
        exception: BaseException,
        details: Map<String, String>? = null,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                code = exception.errorCode,
                message = exception.message ?: exception.errorCode,
                statusCode = exception.statusCode,
                details = details,
            )
        return ResponseEntity(errorResponse, HttpStatus.valueOf(exception.statusCode))
    }

    protected fun getErrorDetails(errorType: BaseErrorDetailType): Map<String, String> = mapOf("details" to errorType.description)

    protected fun <T> createApiErrorResponse(
        exception: BaseException,
        httpStatus: HttpStatus,
    ): ResponseEntity<ApiResponse<T>> =
        ResponseEntity
            .status(httpStatus)
            .body(ApiResponse.error(exception.toErrorDetails()))
}
