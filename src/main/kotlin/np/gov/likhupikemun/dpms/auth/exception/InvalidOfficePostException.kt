package np.gov.likhupikemun.dpms.auth.exception

import np.gov.likhupikemun.dpms.auth.domain.OfficePost
import org.springframework.http.HttpStatus

class InvalidOfficePostException(
    officePost: String,
) : RuntimeException(
        "Invalid office post: $officePost. Valid posts are: ${OfficePost.getAllTitles().joinToString(", ")}",
    ) {
    val errorCode: String = "INVALID_OFFICE_POST"
    val statusCode: Int = HttpStatus.BAD_REQUEST.value()
}
