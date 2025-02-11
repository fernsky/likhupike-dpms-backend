package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseException

class InvalidOfficePostWardCombinationException(
    field: String,
) : BaseException(
        "Invalid sort field: $field",
        AuthErrorCode.INVALID_OFFICE_POST_WARD_COMBINATION.code,
        AuthErrorCode.INVALID_OFFICE_POST_WARD_COMBINATION.statusCode,
    )
