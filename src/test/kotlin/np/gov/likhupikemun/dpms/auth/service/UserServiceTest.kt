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
import np.gov.likhupikemun.dpms.shared.exception.ForbiddenException
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
import java.time.LocalDateTime
import java.time.ZoneOffset
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

    private val municipalityAdminRole =
        Role().apply {
            id = UUID.randomUUID()
            roleType = RoleType.MUNICIPALITY_ADMIN
        }
    private val wardAdminRole =
        Role().apply {
            id = UUID.randomUUID()
            roleType = RoleType.WARD_ADMIN
        }
    private val viewerRole =
        Role().apply {
            id = UUID.randomUUID()
            roleType = RoleType.VIEWER
        }

    private val municipalityAdmin =
        User().apply {
            id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
            email = "admin@municipality.gov.np"
            setPassword("encoded_password")
            fullName = "Municipality Admin"
            fullNameNepali = "नगरपालिका एडमिन"
            dateOfBirth = LocalDate.now()
            address = "Municipality"
            officePost = "Admin"
            isApproved = true
            roles = mutableSetOf(municipalityAdminRole)
            createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        }

    private val wardAdmin =
        User().apply {
            id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001")
            email = "ward1@municipality.gov.np"
            setPassword("encoded_password")
            fullName = "Ward Admin"
            fullNameNepali = "वडा एडमिन"
            dateOfBirth = LocalDate.now()
            address = "Ward 1"
            officePost = "Ward Admin"
            wardNumber = 1
            isApproved = true
            roles = mutableSetOf(wardAdminRole)
            createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        }

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
        every { roleRepository.findByRoleTypeIn(any()) } returns setOf(viewerRole)
        every { userRepository.save(any()) } answers {
            User().apply {
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174002")
                email = firstArg<User>().email
                setPassword(firstArg<User>().password!!)
                fullName = firstArg<User>().fullName
                fullNameNepali = firstArg<User>().fullNameNepali
                dateOfBirth = firstArg<User>().dateOfBirth
                address = firstArg<User>().address
                officePost = firstArg<User>().officePost
                wardNumber = firstArg<User>().wardNumber
                roles = firstArg<User>().roles
                isApproved = firstArg<User>().isApproved
                createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
                updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            }
        }

        // Act
        val result = userService.createUser(request)

        // Assert
        assertNotNull(result)
        assertNotNull(result.id)
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
        assertThrows<ForbiddenException> {
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
        userService.deactivateUser(userToDeactivate.id!!.toString())

        // Assert
        verify { userRepository.save(match { !it.isApproved }) }
    }

    @Test
    fun `deactivateUser - ward admin cannot deactivate municipality admin`() {
        // Arrange
        every { securityService.getCurrentUser() } returns wardAdmin
        every { userRepository.findById(any()) } returns Optional.of(municipalityAdmin)

        // Act & Assert
        assertThrows<ForbiddenException> {
            userService.deactivateUser(municipalityAdmin.id!!.toString())
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
        every { userRepository.findById(any()) } returns
            Optional.of(
                wardAdmin.apply {
                    createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
                    updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
                },
            )
        every { userRepository.save(any()) } answers { firstArg() }

        // Act
        val result = userService.updateUser(wardAdmin.id!!.toString(), updateRequest)

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

    @Test
    fun `approveUser - ward admin cannot approve user from different ward`() {
        // Arrange
        val otherWardUser =
            User().apply {
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174003")
                email = "ward2.viewer@municipality.gov.np"
                setPassword("encoded_password")
                fullName = "Ward 2 Viewer"
                fullNameNepali = "वडा २ भ्युवर"
                dateOfBirth = LocalDate.now()
                address = "Ward 2"
                officePost = "Viewer"
                wardNumber = 2
                isApproved = false
                roles = mutableSetOf(viewerRole)
                createdAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
                updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
            }

        every { securityService.getCurrentUser() } returns wardAdmin
        every { userRepository.findById(any()) } returns Optional.of(otherWardUser)

        // Act & Assert
        assertThrows<ForbiddenException> {
            userService.approveUser(otherWardUser.id!!.toString())
        }
    }
}
