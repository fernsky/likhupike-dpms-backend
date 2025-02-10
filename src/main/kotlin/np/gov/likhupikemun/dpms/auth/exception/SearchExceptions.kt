package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseException

class InvalidSearchCriteriaException(
    message: String,
) : BaseException(message, "INVALID_SEARCH_CRITERIA", 400)

class InvalidSortFieldException(
    field: String,
) : BaseException("Invalid sort field: $field", "INVALID_SORT_FIELD", 400)

class InvalidDateRangeException(
    message: String,
) : BaseException(message, "INVALID_DATE_RANGE", 400)
