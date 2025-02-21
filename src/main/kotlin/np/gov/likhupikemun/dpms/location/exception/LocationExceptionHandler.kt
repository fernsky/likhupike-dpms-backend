package np.gov.likhupikemun.dpms.location.exception

import np.gov.likhupikemun.dpms.shared.exception.*
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class LocationExceptionHandler : BaseExceptionHandler() {
    @ExceptionHandler(
        // Province exceptions
        ProvinceNotFoundException::class,
        ProvinceCodeExistsException::class,
        ProvinceOperationException::class,
        // District exceptions
        DistrictNotFoundException::class,
        DistrictCodeExistsException::class,
        DuplicateDistrictCodeException::class,
        DistrictOperationException::class,
        // Municipality exceptions
        MunicipalityNotFoundException::class,
        DuplicateMunicipalityCodeException::class,
        MunicipalityOperationException::class,
        // Ward exceptions
        WardNotFoundException::class,
        DuplicateWardNumberException::class,
        InvalidWardOperationException::class,
        // Validation exceptions
        InvalidLocationDataException::class,
        // Geographic search exceptions
        GeoSearchException::class,
    )
    fun handleLocationExceptions(
        ex: BaseException,
        request: WebRequest,
    ) = when (ex) {
        // Province handlers
        is ProvinceNotFoundException -> createErrorResponse(ex)
        is ProvinceCodeExistsException -> createErrorResponse(ex)
        is ProvinceOperationException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.OPERATION_NOT_ALLOWED))

        // District handlers
        is DistrictNotFoundException -> createErrorResponse(ex)
        is DistrictCodeExistsException -> createErrorResponse(ex)
        is DuplicateDistrictCodeException -> createErrorResponse(ex)
        is DistrictOperationException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.OPERATION_NOT_ALLOWED))

        // Municipality handlers
        is MunicipalityNotFoundException -> createErrorResponse(ex)
        is DuplicateMunicipalityCodeException -> createErrorResponse(ex)
        is MunicipalityOperationException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.OPERATION_NOT_ALLOWED))

        // Ward handlers
        is WardNotFoundException -> createErrorResponse(ex)
        is DuplicateWardNumberException -> createErrorResponse(ex)
        is InvalidWardOperationException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.OPERATION_NOT_ALLOWED))

        // Validation handlers
        is InvalidLocationDataException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.INVALID_INPUT))

        // Geographic search handlers
        is GeoSearchException -> createErrorResponse(ex, getErrorDetails(SharedErrorDetailType.INVALID_SEARCH_PARAMS))

        else -> createErrorResponse(ex)
    }
}
