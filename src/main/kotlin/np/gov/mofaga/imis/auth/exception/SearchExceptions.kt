package np.gov.mofaga.imis.auth.exception

import np.gov.mofaga.imis.shared.exception.BaseException

class InvalidSearchCriteriaException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.INVALID_SEARCH_CRITERIA.message,
        AuthErrorCode.INVALID_SEARCH_CRITERIA.code,
        AuthErrorCode.INVALID_SEARCH_CRITERIA.statusCode,
    )

class InvalidSortFieldException(
    field: String,
) : BaseException(
        "Invalid sort field: $field",
        AuthErrorCode.INVALID_SORT_FIELD.code,
        AuthErrorCode.INVALID_SORT_FIELD.statusCode,
    )

class InvalidDateRangeException(
    message: String? = null,
) : BaseException(
        message ?: AuthErrorCode.INVALID_DATE_RANGE.message,
        AuthErrorCode.INVALID_DATE_RANGE.code,
        AuthErrorCode.INVALID_DATE_RANGE.statusCode,
    )
