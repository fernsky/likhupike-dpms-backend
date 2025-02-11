package np.gov.likhupikemun.dpms.shared.exception

import np.gov.likhupikemun.dpms.shared.dto.ErrorDetails

/**
 * Base exception class for all custom exceptions in the application.
 * Provides standard fields for error code, message, and HTTP status code.
 */
abstract class BaseException(
    override val message: String,
    val errorCode: String,
    val statusCode: Int,
    val details: Map<String, Any>? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    fun toErrorDetails() =
        ErrorDetails(
            code = errorCode,
            message = message,
            status = statusCode,
            details = details,
        )

    override fun toString(): String = message

    companion object {
        const val DEFAULT_ERROR_CODE = "INTERNAL_SERVER_ERROR"
        const val DEFAULT_STATUS_CODE = 500
    }
}

/**
 * Exception thrown when a resource is not found
 */
class ResourceNotFoundException(
    resourceName: String,
    identifier: String,
) : BaseException(
        message = "$resourceName not found with identifier: $identifier",
        errorCode = "NOT_FOUND",
        statusCode = 404,
    )

/**
 * Exception thrown when the user lacks necessary permissions
 */
class UnauthorizedException(
    message: String,
) : BaseException(
        message = message,
        errorCode = "UNAUTHORIZED",
        statusCode = 403,
    )

/**
 * Exception thrown when an operation is invalid
 */
class InvalidOperationException(
    message: String,
) : BaseException(
        message = message,
        errorCode = "INVALID_OPERATION",
        statusCode = 400,
    )

/**
 * Exception thrown when input validation fails
 */
class ValidationException(
    message: String,
    val errors: Map<String, String>,
) : BaseException(
        message = message,
        errorCode = "VALIDATION_ERROR",
        statusCode = 400,
    )

/**
 * Exception thrown when a conflict occurs (e.g., duplicate entry)
 */
class ConflictException(
    message: String,
) : BaseException(
        message = message,
        errorCode = "CONFLICT",
        statusCode = 409,
    )

/**
 * Exception thrown when bad request is received
 */
class BadRequestException(
    message: String,
) : BaseException(
        message = message,
        errorCode = "BAD_REQUEST",
        statusCode = 400,
    )

/**
 * Exception thrown when input data is invalid
 */
class InvalidInputException(
    message: String,
) : BaseException(
        message = message,
        errorCode = "INVALID_INPUT",
        statusCode = 400,
    )

/**
 * Exception for internal server errors
 */
class InternalServerException(
    message: String,
    details: Map<String, String>? = null,
) : BaseException(
        message = message,
        errorCode = DEFAULT_ERROR_CODE,
        statusCode = DEFAULT_STATUS_CODE,
        details = details,
    )
