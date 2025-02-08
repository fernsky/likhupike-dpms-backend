package np.gov.likhupikemun.dpms.auth.api.dto

import np.gov.likhupikemun.dpms.auth.domain.User
import java.time.format.DateTimeFormatter

fun User.toResponse() =
    UserResponse(
        id = id!!,
        email = email,
        fullName = fullName,
        fullNameNepali = fullNameNepali,
        dateOfBirth = dateOfBirth.format(DateTimeFormatter.ISO_DATE),
        address = address,
        profilePicture = profilePicture,
        officePost = officePost,
        wardNumber = wardNumber,
        isMunicipalityLevel = isMunicipalityLevel,
        isApproved = isApproved,
        roles = roles.map { it.name }.toSet(),
        createdAt = createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = updatedAt?.format(DateTimeFormatter.ISO_DATE_TIME),
    )
