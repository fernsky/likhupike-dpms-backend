package np.gov.likhupikemun.dpms.location.exception

import np.gov.likhupikemun.dpms.shared.exception.BaseException
import org.springframework.http.HttpStatus
import java.util.*

// Province Exceptions
class ProvinceNotFoundException(
    id: UUID,
) : BaseException(
        httpStatus = HttpStatus.NOT_FOUND,
        message = "Province with ID $id not found",
        errorCode = "PROVINCE_NOT_FOUND",
    )

class DuplicateProvinceCodeException(
    code: String,
) : BaseException(
        httpStatus = HttpStatus.CONFLICT,
        message = "Province with code $code already exists",
        errorCode = "DUPLICATE_PROVINCE_CODE",
    )

class ProvinceOperationException(
    message: String,
    errorCode: String = "PROVINCE_OPERATION_ERROR",
) : BaseException(
        httpStatus = HttpStatus.FORBIDDEN,
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun hasActiveDistricts(provinceId: UUID) =
            ProvinceOperationException(
                message = "Cannot deactivate province with ID $provinceId as it has active districts",
                errorCode = "PROVINCE_HAS_ACTIVE_DISTRICTS",
            )
    }
}

// District Exceptions
class DistrictNotFoundException(
    id: UUID,
) : BaseException(
        httpStatus = HttpStatus.NOT_FOUND,
        message = "District with ID $id not found",
        errorCode = "DISTRICT_NOT_FOUND",
    )

class DuplicateDistrictCodeException(
    code: String,
    provinceId: UUID,
) : BaseException(
        httpStatus = HttpStatus.CONFLICT,
        message = "District with code $code already exists in province with ID $provinceId",
        errorCode = "DUPLICATE_DISTRICT_CODE",
    )

class DistrictOperationException(
    message: String,
    errorCode: String = "DISTRICT_OPERATION_ERROR",
) : BaseException(
        httpStatus = HttpStatus.FORBIDDEN,
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun hasActiveMunicipalities(districtId: UUID) =
            DistrictOperationException(
                message = "Cannot deactivate district with ID $districtId as it has active municipalities",
                errorCode = "DISTRICT_HAS_ACTIVE_MUNICIPALITIES",
            )

        fun invalidProvince(
            districtId: UUID,
            provinceId: UUID,
        ) = DistrictOperationException(
            message = "District $districtId does not belong to province $provinceId",
            errorCode = "INVALID_DISTRICT_PROVINCE",
        )
    }
}

// Municipality Exceptions
class MunicipalityNotFoundException(
    id: UUID,
) : BaseException(
        httpStatus = HttpStatus.NOT_FOUND,
        message = "Municipality with ID $id not found",
        errorCode = "MUNICIPALITY_NOT_FOUND",
    )

class DuplicateMunicipalityCodeException(
    code: String,
    districtId: UUID,
) : BaseException(
        httpStatus = HttpStatus.CONFLICT,
        message = "Municipality with code $code already exists in district with ID $districtId",
        errorCode = "DUPLICATE_MUNICIPALITY_CODE",
    )

class MunicipalityOperationException(
    message: String,
    errorCode: String = "MUNICIPALITY_OPERATION_ERROR",
) : BaseException(
        httpStatus = HttpStatus.FORBIDDEN,
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun hasActiveWards(municipalityId: UUID) =
            MunicipalityOperationException(
                message = "Cannot deactivate municipality with ID $municipalityId as it has active wards",
                errorCode = "MUNICIPALITY_HAS_ACTIVE_WARDS",
            )

        fun invalidDistrict(
            municipalityId: UUID,
            districtId: UUID,
        ) = MunicipalityOperationException(
            message = "Municipality $municipalityId does not belong to district $districtId",
            errorCode = "INVALID_MUNICIPALITY_DISTRICT",
        )

        fun accessDenied(municipalityId: UUID) =
            MunicipalityOperationException(
                message = "Access denied to municipality with ID $municipalityId",
                errorCode = "MUNICIPALITY_ACCESS_DENIED",
            )
    }
}

// Ward Exceptions
class WardNotFoundException(
    id: UUID,
) : BaseException(
        httpStatus = HttpStatus.NOT_FOUND,
        message = "Ward with ID $id not found",
        errorCode = "WARD_NOT_FOUND",
    )

class DuplicateWardNumberException(
    wardNumber: Int,
    municipalityId: UUID,
) : BaseException(
        httpStatus = HttpStatus.CONFLICT,
        message = "Ward number $wardNumber already exists in municipality with ID $municipalityId",
        errorCode = "DUPLICATE_WARD_NUMBER",
    )

class InvalidWardOperationException(
    message: String,
    errorCode: String = "INVALID_WARD_OPERATION",
) : BaseException(
        httpStatus = HttpStatus.FORBIDDEN,
        message = message,
        errorCode = errorCode,
    ) {
    companion object {
        fun noAccess(wardId: UUID) =
            InvalidWardOperationException(
                message = "You don't have access to ward with ID $wardId",
                errorCode = "WARD_ACCESS_DENIED",
            )

        fun hasActiveFamilies(wardId: UUID) =
            InvalidWardOperationException(
                message = "Cannot deactivate ward with ID $wardId as it has active families",
                errorCode = "WARD_HAS_ACTIVE_FAMILIES",
            )

        fun alreadyDeactivated(wardId: UUID) =
            InvalidWardOperationException(
                message = "Ward with ID $wardId is already deactivated",
                errorCode = "WARD_ALREADY_DEACTIVATED",
            )

        fun alreadyActive(wardId: UUID) =
            InvalidWardOperationException(
                message = "Ward with ID $wardId is already active",
                errorCode = "WARD_ALREADY_ACTIVE",
            )
    }
}

// Validation Exceptions
class InvalidLocationDataException(
    message: String,
    errorCode: String = "INVALID_LOCATION_DATA",
) : BaseException(
        httpStatus = HttpStatus.BAD_REQUEST,
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
        httpStatus = HttpStatus.BAD_REQUEST,
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
