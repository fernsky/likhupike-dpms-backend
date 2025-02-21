package np.gov.likhupikemun.dpms.location.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseException
import org.springframework.http.HttpStatus

// Province Exceptions
class ProvinceNotFoundException(
    code: String,
) : BaseException(
        statusCode = HttpStatus.NOT_FOUND.value(),
        message = "Province with code $code not found",
        errorCode = "PROVINCE_NOT_FOUND",
    )

class ProvinceCodeExistsException(
    code: String,
) : BaseException(
        statusCode = HttpStatus.CONFLICT.value(),
        message = "Province with code $code already exists",
        errorCode = "DUPLICATE_PROVINCE_CODE",
    )

class ProvinceOperationException(
    message: String,
    errorCode: String = "PROVINCE_OPERATION_ERROR",
) : BaseException(
        statusCode = HttpStatus.FORBIDDEN.value(),
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun hasActiveDistricts(provinceCode: String) =
            ProvinceOperationException(
                message = "Cannot deactivate province with code $provinceCode as it has active districts",
                errorCode = "PROVINCE_HAS_ACTIVE_DISTRICTS",
            )
    }
}

// District Exceptions
class DistrictNotFoundException(
    code: String,
) : BaseException(
        statusCode = HttpStatus.NOT_FOUND.value(),
        message = "District with code $code not found",
        errorCode = "DISTRICT_NOT_FOUND",
    )

class DistrictCodeExistsException(
    code: String,
) : BaseException(
        statusCode = HttpStatus.CONFLICT.value(),
        message = "District with code $code already exists",
        errorCode = "DUPLICATE_DISTRICT_CODE",
    )

class DuplicateDistrictCodeException(
    code: String,
    provinceCode: String,
) : BaseException(
        statusCode = HttpStatus.CONFLICT.value(),
        message = "District with code $code already exists in province $provinceCode",
        errorCode = "DUPLICATE_DISTRICT_CODE",
    )

class DistrictOperationException(
    message: String,
    errorCode: String = "DISTRICT_OPERATION_ERROR",
) : BaseException(
        statusCode = HttpStatus.FORBIDDEN.value(),
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun hasActiveMunicipalities(districtCode: String) =
            DistrictOperationException(
                message = "Cannot deactivate district with code $districtCode as it has active municipalities",
                errorCode = "DISTRICT_HAS_ACTIVE_MUNICIPALITIES",
            )

        fun invalidProvince(
            districtCode: String,
            provinceCode: String,
        ) = DistrictOperationException(
            message = "District $districtCode does not belong to province $provinceCode",
            errorCode = "INVALID_DISTRICT_PROVINCE",
        )
    }
}

// Municipality Exceptions
class MunicipalityNotFoundException(
    code: String,
) : BaseException(
        statusCode = HttpStatus.NOT_FOUND.value(),
        message = "Municipality with code $code not found",
        errorCode = "MUNICIPALITY_NOT_FOUND",
    )

class DuplicateMunicipalityCodeException(
    code: String,
    districtCode: String,
) : BaseException(
        statusCode = HttpStatus.CONFLICT.value(),
        message = "Municipality with code $code already exists in district $districtCode",
        errorCode = "DUPLICATE_MUNICIPALITY_CODE",
    )

class MunicipalityOperationException(
    message: String,
    errorCode: String = "MUNICIPALITY_OPERATION_ERROR",
) : BaseException(
        statusCode = HttpStatus.FORBIDDEN.value(),
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun hasActiveWards(municipalityCode: String) =
            MunicipalityOperationException(
                message = "Cannot deactivate municipality with code $municipalityCode as it has active wards",
                errorCode = "MUNICIPALITY_HAS_ACTIVE_WARDS",
            )

        fun invalidDistrict(
            municipalityCode: String,
            districtCode: String,
        ) = MunicipalityOperationException(
            message = "Municipality $municipalityCode does not belong to district $districtCode",
            errorCode = "INVALID_MUNICIPALITY_DISTRICT",
        )

        fun accessDenied(municipalityCode: String) =
            MunicipalityOperationException(
                message = "Access denied to municipality with code $municipalityCode",
                errorCode = "MUNICIPALITY_ACCESS_DENIED",
            )
    }
}

// Ward Exceptions
class WardNotFoundException(
    municipalityCode: String,
    wardNumber: Int,
) : BaseException(
        statusCode = HttpStatus.NOT_FOUND.value(),
        message = "Ward number $wardNumber not found in municipality $municipalityCode",
        errorCode = "WARD_NOT_FOUND",
    )

class DuplicateWardNumberException(
    wardNumber: Int,
    municipalityCode: String,
) : BaseException(
        statusCode = HttpStatus.CONFLICT.value(),
        message = "Ward number $wardNumber already exists in municipality $municipalityCode",
        errorCode = "DUPLICATE_WARD_NUMBER",
    )

// Validation Exceptions
class InvalidLocationDataException(
    message: String,
    errorCode: String = "INVALID_LOCATION_DATA",
) : BaseException(
        statusCode = HttpStatus.BAD_REQUEST.value(),
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun invalidCoordinates(
            latitude: Double,
            longitude: Double,
        ) = InvalidLocationDataException(
            message = "Invalid coordinates: ($latitude, $longitude)",
            errorCode = "INVALID_COORDINATES",
        )

        fun invalidPopulation(value: Long) =
            InvalidLocationDataException(
                message = "Invalid population value: $value",
                errorCode = "INVALID_POPULATION",
            )

        fun invalidArea(value: Double) =
            InvalidLocationDataException(
                message = "Invalid area value: $value",
                errorCode = "INVALID_AREA",
            )

        fun invalidWardCount(value: Int) =
            InvalidLocationDataException(
                message = "Invalid ward count: $value",
                errorCode = "INVALID_WARD_COUNT",
            )
    }
}

// Geographic Search Exceptions
class GeoSearchException(
    message: String,
    errorCode: String = "GEO_SEARCH_ERROR",
) : BaseException(
        statusCode = HttpStatus.BAD_REQUEST.value(),
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun invalidRadius(radius: Double) =
            GeoSearchException(
                message = "Invalid search radius: $radius",
                errorCode = "INVALID_SEARCH_RADIUS",
            )

        fun exceededMaxRadius(
            radius: Double,
            maxRadius: Double,
        ) = GeoSearchException(
            message = "Search radius $radius exceeds maximum allowed radius of $maxRadius",
            errorCode = "EXCEEDED_MAX_RADIUS",
        )
    }
}
