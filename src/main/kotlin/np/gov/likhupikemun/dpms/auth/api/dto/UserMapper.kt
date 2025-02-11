package np.gov.likhupikemun.dpms.auth.api.dto

import np.gov.likhupikemun.dpms.auth.api.dto.response.UserResponse
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import java.time.LocalDateTime

fun User.toResponse(): UserResponse {
    val dtoRoles = roles.map { role -> RoleType.valueOf(role.roleType.name) }.toSet()

    return UserResponse(
        id = id!!,
        email = email,
        fullName = fullName,
        fullNameNepali = fullNameNepali,
        wardNumber = wardNumber,
        officePost = officePost,
        roles = dtoRoles,
        status = if (isApproved) UserStatus.ACTIVE else UserStatus.PENDING,
        profilePictureUrl = profilePicture?.let { "/uploads/profiles/$it" },
        isMunicipalityLevel = isMunicipalityLevel,
        createdAt = createdAt ?: LocalDateTime.now(),
        updatedAt = updatedAt ?: LocalDateTime.now(),
    )
}
