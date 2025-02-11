package np.gov.likhupikemun.dpms.auth.test

import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import java.time.LocalDate
import java.time.LocalDateTime

object UserTestDataFactory {
    fun createMunicipalityAdmin(
        id: String = "1",
        email: String = "admin@municipality.gov.np",
    ) = User(
        id = id,
        email = email,
        password = "encoded_password",
        fullName = "Municipality Admin",
        fullNameNepali = "नगरपालिका एडमिन",
        dateOfBirth = LocalDate.now(),
        address = "Municipality",
        officePost = "Admin",
        isMunicipalityLevel = true,
        isApproved = true,
        roles = mutableSetOf(Role("1", RoleType.MUNICIPALITY_ADMIN)),
    ).apply {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    fun createWardAdmin(
        id: String = "2",
        email: String = "ward.admin@municipality.gov.np",
        wardNumber: Int = 1,
    ) = User(
        id = id,
        email = email,
        password = "encoded_password",
        fullName = "Ward Admin",
        fullNameNepali = "वडा एडमिन",
        dateOfBirth = LocalDate.now(),
        address = "Ward $wardNumber",
        officePost = "Ward Admin",
        wardNumber = wardNumber,
        isMunicipalityLevel = false,
        isApproved = true,
        roles = mutableSetOf(Role("2", RoleType.WARD_ADMIN)),
    ).apply {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    fun createViewer(
        id: String = "3",
        email: String = "viewer@municipality.gov.np",
        wardNumber: Int? = null,
        isMunicipalityLevel: Boolean = false,
    ) = User(
        id = id,
        email = email,
        password = "encoded_password",
        fullName = "Viewer User",
        fullNameNepali = "दर्शक प्रयोगकर्ता",
        dateOfBirth = LocalDate.now(),
        address = wardNumber?.let { "Ward $it" } ?: "Municipality",
        officePost = "Viewer",
        wardNumber = wardNumber,
        isMunicipalityLevel = isMunicipalityLevel,
        isApproved = true,
        roles = mutableSetOf(Role("3", RoleType.VIEWER)),
    ).apply {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }
}
