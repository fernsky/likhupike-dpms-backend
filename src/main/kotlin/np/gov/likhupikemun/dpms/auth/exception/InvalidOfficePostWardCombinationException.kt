package np.gov.likhupikemun.dpms.auth.exception

import org.springframework.http.HttpStatus

class InvalidOfficePostWardCombinationException :
    RuntimeException(
        "Chief Administrative Officer cannot be assigned to a specific ward",
    ) {
    val errorCode: String = "INVALID_OFFICE_POST_WARD_COMBINATION"
    val statusCode: Int = HttpStatus.BAD_REQUEST.value()
}
