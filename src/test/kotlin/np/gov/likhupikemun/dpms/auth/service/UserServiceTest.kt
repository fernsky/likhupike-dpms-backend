package np.gov.likhupikemun.dpms.auth.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import np.gov.likhupikemun.dpms.auth.api.dto.UpdateUserRequest
import np.gov.likhupikemun.dpms.auth.api.dto.request.CreateUserRequest
import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSearchCriteria
import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSortField
import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import np.gov.likhupikemun.dpms.auth.exception.EmailAlreadyExistsException
import np.gov.likhupikemun.dpms.auth.exception.UserNotFoundException
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.RoleRepository
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.UserRepository
import np.gov.likhupikemun.dpms.shared.exception.UnauthorizedException
import np.gov.likhupikemun.dpms.shared.service.FileService
import np.gov.likhupikemun.dpms.shared.service.SecurityService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserServiceTest {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var roleRepository: RoleRepository

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var fileService: FileService

    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var userService: UserService

    private val municipalityAdminRole = Role("1", RoleType.MUNICIPALITY_ADMIN)
    private val wardAdminRole = Role("2", RoleType.WARD_ADMIN)
    private val viewerRole = Role("3", RoleType.VIEWER)

    private val municipalityAdmin =
        User(
            id = "1",
            email = "admin@municipality.gov.np",
            password = "encoded_password",
            fullName = "Municipality Admin",
            fullNameNepali = "नगरपालिका एडमिन",
            dateOfBirth = LocalDate.now(),
            address = "Municipality",
            officePost = "Admin",
            isApproved = true,
            roles = mutableSetOf(municipalityAdminRole),
        )

    private val wardAdmin =
        User(
            id = "2",
            email = "ward1@municipality.gov.np",
            password = "encoded_password",
            fullName = "Ward Admin",
            fullNameNepali = "वडा एडमिन",
            dateOfBirth = LocalDate.now(),
            address = "Ward 1",
            officePost = "Ward Admin",
            wardNumber = 1,
            isApproved = true,
            roles = mutableSetOf(wardAdminRole),
        )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `createUser - municipality admin can create municipality level user`() {
        // Arrange
        val request =
            CreateUserRequest(
                email = "new@municipality.gov.np",
                password = "password123",
                fullName = "New User",
                fullNameNepali = "नयाँ प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Municipality",
                officePost = "Officer",
                isMunicipalityLevel = true,
                roles = setOf(RoleType.VIEWER),
                wardNumber = null,
                profilePicture = null,
            )

        every { securityService.getCurrentUser() } returns municipalityAdmin
        every { userRepository.existsByEmail(any()) } returns false
        every { passwordEncoder.encode(any()) } returns "encoded_password"
        every { roleRepository.findByNameIn(any()) } returns listOf(viewerRole)
        every { userRepository.save(any()) } answers { firstArg() }

        // Act
        val result = userService.createUser(request)

        // Assert
        assertNotNull(result)
        verify { userRepository.save(any()) }
    }

    @Test
    fun `createUser - ward admin can only create users for their ward`() {
        // Arrange
        val request =
            CreateUserRequest(
                email = "new@ward2.municipality.gov.np",
                password = "password123",
                fullName = "New User",
                fullNameNepali = "नयाँ प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Ward 2",
                officePost = "Officer",
                isMunicipalityLevel = false,
                roles = setOf(RoleType.VIEWER),
                wardNumber = 2, // Different ward than admin's ward
                profilePicture = null,
            )

        every { securityService.getCurrentUser() } returns wardAdmin

        // Act & Assert
        assertThrows<UnauthorizedException> {
            userService.createUser(request)
        }
    }

    @Test
    fun `createUser - throws exception when email already exists`() {
        // Arrange
        val request =
            CreateUserRequest(
                email = "existing@municipality.gov.np",
                password = "password123",
                fullName = "New User",
                fullNameNepali = "नयाँ प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Municipality",
                officePost = "Officer",
                isMunicipalityLevel = true,
                roles = setOf(RoleType.VIEWER),
                wardNumber = null,
                profilePicture = null,
            )

        every { securityService.getCurrentUser() } returns municipalityAdmin
        every { userRepository.existsByEmail(any()) } returns true

        // Act & Assert
        assertThrows<EmailAlreadyExistsException> {
            userService.createUser(request)
        }
    }

    @Test
    fun `searchUsers - municipality admin can search all users`() {
        // Arrange
        val criteria =
            UserSearchCriteria(
                page = 0,
                pageSize = 10,
                sortBy = UserSortField.CREATED_AT,
                isMunicipalityLevel = true,
            )
        val users = listOf(municipalityAdmin, wardAdmin)
        val page = PageImpl(users)

        every { securityService.getCurrentUser() } returns municipalityAdmin
        every { userRepository.findAll(any<Specification<User>>(), any<Pageable>()) } returns page

        // Act
        val result = userService.searchUsers(criteria)

        // Assert
        assertEquals(2, result.content.size)
        verify { userRepository.findAll(any<Specification<User>>(), any<Pageable>()) }
    }

    @Test
    fun `deactivateUser - municipality admin can deactivate any user`() {
        // Arrange
        val userToDeactivate = wardAdmin

        every { securityService.getCurrentUser() } returns municipalityAdmin
        every { userRepository.findById(any()) } returns Optional.of(userToDeactivate)
        every { userRepository.save(any()) } returns userToDeactivate

        // Act
        userService.deactivateUser(userToDeactivate.id!!)

        // Assert
        verify { userRepository.save(match { !it.isApproved }) }
    }

    @Test
    fun `deactivateUser - ward admin cannot deactivate municipality admin`() {
        // Arrange
        every { securityService.getCurrentUser() } returns wardAdmin
        every { userRepository.findById(any()) } returns Optional.of(municipalityAdmin)

        // Act & Assert
        assertThrows<UnauthorizedException> {
            userService.deactivateUser(municipalityAdmin.id!!)
        }
    }

    @Test
    fun `updateUser - can update user details`() {
        // Arrange
        val updateRequest =
            UpdateUserRequest(
                fullName = "Updated Name",
                fullNameNepali = "अपडेट नाम",
                address = "Updated Address",
                officePost = "Updated Post",
                profilePicture = null,
                roles = null,
            )

        every { securityService.getCurrentUser() } returns municipalityAdmin
        every { userRepository.findById(any()) } returns Optional.of(wardAdmin)
        every { userRepository.save(any()) } answers { firstArg() }

        // Act
        val result = userService.updateUser(wardAdmin.id!!, updateRequest)

        // Assert
        assertEquals("Updated Name", result.fullName)
        verify { userRepository.save(any()) }
    }

    @Test
    fun `updateUser - throws exception when user not found`() {
        // Arrange
        val updateRequest =
            UpdateUserRequest(
                fullName = "Updated Name",
                fullNameNepali = "अपडेट नाम",
                address = "Updated Address",
                officePost = "Updated Post",
                profilePicture = null,
                roles = null,
            )

        every { securityService.getCurrentUser() } returns municipalityAdmin
        every { userRepository.findById(any()) } returns Optional.empty()

        // Act & Assert
        assertThrows<UserNotFoundException> {
            userService.updateUser("non-existent-id", updateRequest)
        }
    }
}
