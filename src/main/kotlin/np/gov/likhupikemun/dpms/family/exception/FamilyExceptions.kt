package np.gov.likhupikemun.dpms.family.exception

class FamilyNotFoundException(
    message: String = FamilyErrorCode.FAMILY_NOT_FOUND.message,
) : RuntimeException(message)

class FamilyPhotoNotFoundException(
    message: String = FamilyErrorCode.FAMILY_PHOTO_NOT_FOUND.message,
) : RuntimeException(message)

class InvalidCoordinatesException(
    message: String = FamilyErrorCode.INVALID_COORDINATES.message,
) : RuntimeException(message)

class InvalidFileTypeException(
    message: String = FamilyErrorCode.INVALID_FILE_TYPE.message,
) : RuntimeException(message)

class FileTooLargeException(
    message: String = FamilyErrorCode.FILE_TOO_LARGE.message,
) : RuntimeException(message)
