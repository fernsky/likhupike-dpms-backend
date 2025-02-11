package np.gov.likhupikemun.dpms.auth.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.auth.api.dto.UserStatus
import np.gov.likhupikemun.dpms.auth.api.dto.request.CreateUserRequest
import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSearchCriteria
import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSortField
import np.gov.likhupikemun.dpms.auth.api.dto.response.UserResponse
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.exception.EmailAlreadyExistsException
import np.gov.likhupikemun.dpms.auth.exception.UserNotFoundException
import np.gov.likhupikemun.dpms.auth.service.UserService
import np.gov.likhupikemun.dpms.config.TestConfig
import np.gov.likhupikemun.dpms.shared.exception.UnauthorizedException
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(UserController::class)
@Import(TestConfig::class)
@ActiveProfiles("test")
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    private val testUserResponse =
        UserResponse(
            id = "1",
            email = "test@example.com",
            fullName = "Test User",
            fullNameNepali = "टेस्ट युजर",
            wardNumber = 1,
            officePost = "Officer",
            roles = setOf(RoleType.VIEWER),
            status = UserStatus.ACTIVE,
            profilePictureUrl = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `createUser - should return 200 when municipality admin creates user`() {
        // Arrange
        val request =
            CreateUserRequest(
                email = "new@municipality.gov.np",
                password = "Password123#",
                fullName = "New User",
                fullNameNepali = "नयाँ प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Test Address",
                officePost = "Officer",
                wardNumber = null,
                isMunicipalityLevel = true,
                roles = setOf(RoleType.VIEWER),
                profilePicture = null,
            )

        whenever(userService.createUser(any())).thenReturn(testUserResponse)

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(testUserResponse.id))
            .andExpect(jsonPath("$.data.email").value(testUserResponse.email))
    }

    @Test
    @WithMockUser(roles = ["WARD_ADMIN"])
    fun `createUser - should return 403 when ward admin creates municipality level user`() {
        // Arrange
        val request =
            CreateUserRequest(
                email = "new@municipality.gov.np",
                password = "Password123#",
                fullName = "New User",
                fullNameNepali = "नयाँ प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Test Address",
                officePost = "Officer",
                wardNumber = null,
                isMunicipalityLevel = true,
                roles = setOf(RoleType.VIEWER),
                profilePicture = null,
            )

        whenever(userService.createUser(any())).thenThrow(UnauthorizedException("Not authorized"))

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `createUser - should return 409 when email already exists`() {
        // Arrange
        val request =
            CreateUserRequest(
                email = "existing@municipality.gov.np",
                password = "Password123#",
                fullName = "New User",
                fullNameNepali = "नयाँ प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Test Address",
                officePost = "Officer",
                wardNumber = null,
                isMunicipalityLevel = true,
                roles = setOf(RoleType.VIEWER),
                profilePicture = null,
            )

        whenever(userService.createUser(any())).thenThrow(EmailAlreadyExistsException(request.email))

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isConflict)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `searchUsers - should return 200 with paged results`() {
        // Arrange
        val criteria =
            UserSearchCriteria(
                page = 0,
                pageSize = 10,
                sortBy = UserSortField.CREATED_AT,
                isMunicipalityLevel = true,
            )

        val pagedResponse =
            PageImpl(
                listOf(testUserResponse),
                org.springframework.data.domain.PageRequest
                    .of(0, 10),
                1,
            )
        whenever(userService.searchUsers(any())).thenReturn(pagedResponse)

        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .param("sortBy", "CREATED_AT")
                    .param("sortDirection", "DESC")
                    .param("isMunicipalityLevel", "true"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.content[0].id").value(testUserResponse.id))
            .andExpect(jsonPath("$.data.content[0].email").value(testUserResponse.email))
            // PagedResponse fields
            .andExpect(jsonPath("$.data.totalElements").value(1))
            .andExpect(jsonPath("$.data.totalPages").value(1))
            .andExpect(jsonPath("$.data.pageNumber").value(0))
            .andExpect(jsonPath("$.data.pageSize").value(10))
            .andExpect(jsonPath("$.data.isFirst").value(true))
            .andExpect(jsonPath("$.data.isLast").value(true))
            .andExpect(jsonPath("$.data.hasNext").value(false))
            .andExpect(jsonPath("$.data.hasPrevious").value(false))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `approveUser - should return 200 when approval is successful`() {
        // Arrange
        whenever(userService.approveUser(any())).thenReturn(testUserResponse)

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users/1/approve"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(testUserResponse.id))
            .andExpect(jsonPath("$.data.email").value(testUserResponse.email))
    }

    @Test
    @WithMockUser(roles = ["WARD_ADMIN"])
    fun `approveUser - should return 403 when ward admin approves municipality user`() {
        // Arrange
        whenever(userService.approveUser(any())).thenThrow(UnauthorizedException("Not authorized"))

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users/1/approve"),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `deactivateUser - should return 200 when deactivation is successful`() {
        // Arrange
        doNothing().whenever(userService).deactivateUser(any())

        // Act & Assert
        mockMvc
            .perform(
                delete("/api/v1/users/1"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `deactivateUser - should return 404 when user not found`() {
        // Arrange
        doThrow(UserNotFoundException("1")).whenever(userService).deactivateUser(any())

        // Act & Assert
        mockMvc
            .perform(
                delete("/api/v1/users/1"),
            ).andExpect(status().isNotFound)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `deleteUser - should return 200 when deletion is successful`() {
        // Arrange
        doNothing().whenever(userService).safeDeleteUser(any())

        // Act & Assert
        mockMvc
            .perform(
                delete("/api/v1/users/1/delete"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    @WithMockUser(roles = ["WARD_ADMIN"])
    fun `deleteUser - should return 403 when ward admin deletes municipality user`() {
        // Arrange
        doThrow(UnauthorizedException("Not authorized")).whenever(userService).safeDeleteUser(any())

        // Act & Assert
        mockMvc
            .perform(
                delete("/api/v1/users/1/delete"),
            ).andExpect(status().isForbidden)
    }
}
