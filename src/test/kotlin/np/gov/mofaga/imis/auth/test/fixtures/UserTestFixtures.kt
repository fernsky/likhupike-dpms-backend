package np.gov.mofaga.imis.auth.test.fixtures

import np.gov.mofaga.imis.auth.api.dto.*
import np.gov.mofaga.imis.auth.domain.*
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import java.time.LocalDate

object UserTestFixtures {
    fun createRegisterRequest(
        email: String = "test@example.com",
        password: String = "Test123#@",
        fullName: String = "Test User",
        fullNameNepali: String = "टेस्ट युजर",
        dateOfBirth: LocalDate = LocalDate.of(1990, 1, 1),
        address: String = "Test Address",
        userType: UserType = UserType.LOCAL_LEVEL_EMPLOYEE,
        provinceCode: String = "P1",
        districtCode: String = "D1",
        municipalityCode: String = "M1",
        wardNumber: Int = 1,
        officePost: String = "Manager",
        officeSection: OfficeSection? = OfficeSection.GENERAL_ADMINISTRATION,
        electedPosition: ElectedPosition? = null,
    ) = RegisterRequest(
        email = email,
        password = password,
        fullName = fullName,
        fullNameNepali = fullNameNepali,
        dateOfBirth = dateOfBirth,
        address = address,
        userType = userType,
        provinceCode = provinceCode,
        districtCode = districtCode,
        municipalityCode = municipalityCode,
        wardNumber = wardNumber,
        officePost = officePost,
        officeSection = officeSection,
        electedPosition = electedPosition,
    )

    fun createUser(
        email: String = "test@example.com",
        password: String = "Test123#@",
        fullName: String = "Test User",
        fullNameNepali: String = "टेस्ट युजर",
        dateOfBirth: LocalDate = LocalDate.of(1990, 1, 1),
        address: String = "Test Address",
        userType: UserType = UserType.LOCAL_LEVEL_EMPLOYEE,
        officeSection: OfficeSection? = OfficeSection.GENERAL_ADMINISTRATION,
        electedPosition: ElectedPosition? = null,
        municipality: np.gov.mofaga.imis.location.domain.Municipality = MunicipalityTestFixtures.createMunicipality(),
        wardNumber: Int = 1,
        officePost: String = "Manager",
        isApproved: Boolean = false,
    ) = User().apply {
        this.email = email
        this.setPassword(password)
        this.fullName = fullName
        this.fullNameNepali = fullNameNepali
        this.dateOfBirth = dateOfBirth
        this.address = address
        this.userType = userType
        this.officeSection = officeSection
        this.electedPosition = electedPosition
        this.municipality = municipality
        this.district = municipality.district
        this.province = municipality.district?.province
        this.wardNumber = wardNumber
        this.officePost = officePost
        this.isApproved = isApproved
    }

    fun createAuthResponse(
        userId: String = "test-id",
        email: String = "test@example.com",
        token: String = "test-token",
        refreshToken: String = "refresh-token",
        roles: Set<RoleType> = setOf(RoleType.EDITOR),
        expiresIn: Long = 3600L,
    ) = AuthResponse(
        userId = userId,
        email = email,
        token = token,
        refreshToken = refreshToken,
        roles = roles,
        expiresIn = expiresIn,
    )
}
