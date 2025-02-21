package np.gov.likhupikemun.dpms.shared.exception

enum class SharedErrorDetailType(
    override val description: String,
) : BaseErrorDetailType {
    // Authentication & Authorization
    UNAUTHORIZED_ACCESS("You do not have permission to perform this action"),
    INVALID_CREDENTIALS("The provided credentials are invalid"),
    TOKEN_EXPIRED("The authentication token has expired"),

    // Resource handling
    RESOURCE_NOT_FOUND("The requested resource could not be found"),
    RESOURCE_ALREADY_EXISTS("A resource with these details already exists"),
    RESOURCE_IN_USE("This resource is currently in use and cannot be modified"),

    // Data validation
    INVALID_INPUT_FORMAT("The provided input format is invalid"),
    MISSING_REQUIRED_FIELD("One or more required fields are missing"),
    CONSTRAINT_VIOLATION("The operation violates data constraints"),

    // Search and filtering
    INVALID_SEARCH_PARAMS("The search criteria provided is invalid"),
    INVALID_SORT_FIELD("The specified sort field is not valid for this search"),
    INVALID_DATE_RANGE("The provided date range is invalid"),

    // Database operations
    DATABASE_ERROR("An error occurred while accessing the database"),
    DUPLICATE_ENTRY("An entry with this information already exists"),

    // General
    OPERATION_FAILED("The requested operation failed to complete"),
    INVALID_STATE("The operation cannot be performed in the current state"),
    SYSTEM_ERROR("An internal system error occurred"),

    // Operation restrictions
    OPERATION_NOT_ALLOWED("This operation is not allowed in the current context"),
    INVALID_INPUT("The provided input data is invalid"),
}
