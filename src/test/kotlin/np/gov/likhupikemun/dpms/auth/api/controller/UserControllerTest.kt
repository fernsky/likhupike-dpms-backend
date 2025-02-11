package np.gov.likhupikemun.dpms.auth.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.auth.api.dto.request.CreateUserRequest
import np.gov.likhupikemun.dpms.auth.api.dto.response.UserResponse
import np.gov.likhupikemun.dpms.auth.api.dto.toResponse
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import np.gov.likhupikemun.dpms.auth.service.UserService
import np.gov.likhupikemun.dpms.auth.test.UserTestDataFactory
import np.gov.likhupikemun.dpms.config.TestSecurityConfig
import np.gov.likhupikemun.dpms.shared.service.SecurityService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(UserController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    @MockBean
    private lateinit var securityService: SecurityService

    // Test users via factory
    private val municipalityAdmin =
        UserTestDataFactory.createMunicipalityAdmin(
            id = "1",
            email = "admin@municipality.gov.np",
        )

    private val wardAdmin =
        UserTestDataFactory.createWardAdmin(
            id = "2",
            email = "ward1.admin@municipality.gov.np",
            wardNumber = 1,
        )

    private val municipalityViewer =
        UserTestDataFactory.createViewer(
            id = "3",
            email = "viewer@municipality.gov.np",
            isMunicipalityLevel = true,
        )

    private val wardViewer =
        UserTestDataFactory.createViewer(
            id = "4",
            email = "ward1.viewer@municipality.gov.np",
            wardNumber = 1,
            isMunicipalityLevel = false,
        )

    private fun mockLoggedInUser(user: User) {
        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        SecurityContextHolder.getContext().authentication = authentication
        whenever(securityService.getCurrentUser()).thenReturn(user)
    }

    @Test
    fun `createUser - municipality admin can create municipality level user`() {
        // Arrange
        mockLoggedInUser(municipalityAdmin)
        val request =
            CreateUserRequest(
                email = "new@municipality.gov.np",
                password = "Password123#",
                fullName = "New Municipality User",
                fullNameNepali = "नयाँ नगरपालिका प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Municipality",
                officePost = "Officer",
                wardNumber = null,
                isMunicipalityLevel = true,
                roles = setOf(RoleType.VIEWER),
                profilePicture = null,
            )

        whenever(userService.createUser(any())).thenReturn(municipalityViewer.toResponse())

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.email").value(municipalityViewer.email))
            .andExpect(jsonPath("$.data.isMunicipalityLevel").value(true))
    }

    @Test
    fun `createUser - ward admin can create ward level user`() {
        // Arrange
        mockLoggedInUser(wardAdmin)
        val request =
            CreateUserRequest(
                email = "new.ward1@municipality.gov.np",
                password = "Password123#",
                fullName = "New Ward User",
                fullNameNepali = "नयाँ वडा प्रयोगकर्ता",
                dateOfBirth = LocalDate.now().toString(),
                address = "Ward 1",
                officePost = "Officer",
                wardNumber = 1,
                isMunicipalityLevel = false,
                roles = setOf(RoleType.VIEWER),
                profilePicture = null,
            )

        whenever(userService.createUser(any())).thenReturn(wardViewer.toResponse())

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(1))
            .andExpect(jsonPath("$.data.isMunicipalityLevel").value(false))
    }

    @Test
    fun `searchUsers - municipality admin can search all users`() {
        // Arrange
        mockLoggedInUser(municipalityAdmin)
        val pagedResponse =
            PageImpl<UserResponse>(
                listOf(
                    municipalityViewer.toResponse(),
                    wardViewer.toResponse(),
                ),
            )
        whenever(userService.searchUsers(any())).thenReturn(pagedResponse)

        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("isMunicipalityLevel", "true"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].email").value(municipalityViewer.email))
            .andExpect(jsonPath("$.data.content[1].email").value(wardViewer.email))
    }

    @Test
    fun `searchUsers - ward admin can only search their ward users`() {
        // Arrange
        mockLoggedInUser(wardAdmin)
        val pagedResponse = PageImpl<UserResponse>(listOf(wardViewer.toResponse()))
        whenever(userService.searchUsers(any())).thenReturn(pagedResponse)

        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("wardNumber", "1"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].wardNumber").value(1))
    }

    @Test
    fun `approveUser - municipality admin can approve any user`() {
        // Arrange
        mockLoggedInUser(municipalityAdmin)
        whenever(userService.approveUser(wardViewer.id!!)).thenReturn(wardViewer.toResponse())

        // Act & Assert
        mockMvc
            .perform(post("/api/v1/users/${wardViewer.id}/approve"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.email").value(wardViewer.email))
    }

    // TODO [TEST] ward admin can only approve their ward users

    // @Test
    // fun `approveUser - ward admin can only approve their ward users`() {
    //     // Arrange
    //     logger.debug("Setting up ward admin approval test")

    //     mockLoggedInUser(wardAdmin)
    //     logger.debug("Mocked logged in ward admin: {} (Ward: {})", wardAdmin.email, wardAdmin.wardNumber)

    //     val otherWardUser =
    //         UserTestDataFactory.createViewer(
    //             id = "other-ward-user",
    //             email = "ward2.viewer@municipality.gov.np",
    //             wardNumber = 2,
    //             isMunicipalityLevel = false,
    //         )
    //     logger.debug("Created test user from different ward: {}", otherWardUser.toResponse())
    //     logger.debug("Ward admin attempting approval: {}", wardAdmin.toResponse())

    //     // Act & Assert
    //     logger.debug("Executing approval request")
    //     mockMvc
    //         .perform(post("/api/v1/users/${otherWardUser.id}/approve"))
    //         .andDo { result ->
    //             logger.debug("Response status: {}", result.response.status)
    //             logger.debug("Response body: {}", result.response.contentAsString)
    //         }.andExpect(status().isForbidden)

    //     logger.debug("Test completed")
    // }

    companion object {
        private val logger = LoggerFactory.getLogger(UserControllerTest::class.java)
    }
}
