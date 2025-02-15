package np.gov.likhupikemun.dpms.auth.test

import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

object UserTestDataFactory {
    private fun createRole(roleType: RoleType): Role =
        Role().apply {
            id = UUID.randomUUID()
            this.roleType = roleType
        }

    fun createMunicipalityAdmin(
        id: String = "1",
        email: String = "admin@municipality.gov.np",
    ) = User().apply {
        this.id = UUID.fromString(id)
        this.email = email
        setPassword("encoded_password")
        fullName = "Municipality Admin"
        fullNameNepali = "नगरपालिका एडमिन"
        dateOfBirth = LocalDate.now()
        address = "Municipality"
        officePost = "Admin"
        isMunicipalityLevel = true
        isApproved = true
        roles = mutableSetOf(createRole(RoleType.MUNICIPALITY_ADMIN))
        createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
    }

    fun createWardAdmin(
        id: String = "2",
        email: String = "ward.admin@municipality.gov.np",
        wardNumber: Int = 1,
    ) = User().apply {
        this.id = UUID.fromString(id)
        this.email = email
        setPassword("encoded_password")
        fullName = "Ward Admin"
        fullNameNepali = "वडा एडमिन"
        dateOfBirth = LocalDate.now()
        address = "Ward $wardNumber"
        officePost = "Ward Admin"
        this.wardNumber = wardNumber
        isMunicipalityLevel = false
        isApproved = true
        roles = mutableSetOf(createRole(RoleType.WARD_ADMIN))
        createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
    }

    fun createViewer(
        id: String = "3",
        email: String = "viewer@municipality.gov.np",
        wardNumber: Int? = null,
        isMunicipalityLevel: Boolean = false,
    ) = User().apply {
        this.id = UUID.fromString(id)
        this.email = email
        setPassword("encoded_password")
        fullName = "Viewer User"
        fullNameNepali = "दर्शक प्रयोगकर्ता"
        dateOfBirth = LocalDate.now()
        address = wardNumber?.let { "Ward $it" } ?: "Municipality"
        officePost = "Viewer"
        this.wardNumber = wardNumber
        this.isMunicipalityLevel = isMunicipalityLevel
        isApproved = true
        roles = mutableSetOf(createRole(RoleType.VIEWER))
        createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
    }
}
