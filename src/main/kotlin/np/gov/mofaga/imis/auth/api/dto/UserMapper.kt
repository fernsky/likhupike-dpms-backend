package np.gov.mofaga.imis.auth.api.dto

import np.gov.mofaga.imis.auth.api.dto.UserStatus
import np.gov.mofaga.imis.auth.api.dto.response.UserResponse
import np.gov.mofaga.imis.auth.domain.User
import java.time.LocalDateTime
import java.time.ZoneId

fun User.toResponse(): UserResponse {
    val dtoRoles = roles.mapNotNull { role -> role.roleType }.toSet()
    val now = LocalDateTime.now(ZoneId.systemDefault())

    return UserResponse(
        id = id ?: throw IllegalStateException("User ID cannot be null"),
        email = email ?: throw IllegalStateException("Email cannot be null"),
        fullName = fullName ?: throw IllegalStateException("Full name cannot be null"),
        fullNameNepali = fullNameNepali ?: throw IllegalStateException("Nepali full name cannot be null"),
        wardNumber = wardNumber,
        officePost = officePost,
        roles = dtoRoles,
        status = if (isApproved) UserStatus.ACTIVE else UserStatus.PENDING,
        profilePictureUrl = profilePicture?.let { "/uploads/profiles/$it" },
        isMunicipalityLevel = isMunicipalityLevel,
        createdAt = createdAt?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) } ?: now,
        updatedAt = updatedAt?.let { LocalDateTime.ofInstant(it, ZoneId.systemDefault()) } ?: now,
    )
}
