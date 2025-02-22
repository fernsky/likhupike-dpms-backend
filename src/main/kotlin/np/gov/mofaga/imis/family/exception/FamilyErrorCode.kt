package np.gov.mofaga.imis.family.exception

enum class FamilyErrorCode(
    val message: String,
) {
    FAMILY_NOT_FOUND("Family not found"),
    FAMILY_PHOTO_NOT_FOUND("Family photo not found"),
    INVALID_COORDINATES("Invalid GPS coordinates provided"),
    INVALID_FILE_TYPE("Invalid file type. Only images are allowed"),
    FILE_TOO_LARGE("File size exceeds maximum limit"),
}
