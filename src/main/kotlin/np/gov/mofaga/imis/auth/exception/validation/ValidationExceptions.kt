package np.gov.mofaga.imis.auth.exception

import np.gov.mofaga.imis.shared.exception.BaseException

class InvalidOfficePostWardCombinationException(
    field: String,
) : BaseException(
        "Invalid sort field: $field",
        AuthErrorCode.INVALID_OFFICE_POST_WARD_COMBINATION.code,
        AuthErrorCode.INVALID_OFFICE_POST_WARD_COMBINATION.statusCode,
    )
