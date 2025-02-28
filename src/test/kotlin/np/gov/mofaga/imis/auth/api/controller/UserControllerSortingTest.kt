package np.gov.mofaga.imis.auth.api.controller

import np.gov.mofaga.imis.auth.api.dto.UserStatus
import np.gov.mofaga.imis.auth.api.dto.response.UserResponse
import np.gov.mofaga.imis.auth.domain.RoleType
import np.gov.mofaga.imis.auth.service.UserService
import np.gov.mofaga.imis.config.TestSecurityConfig
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime
import java.util.UUID

@WebMvcTest(UserController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
class UserControllerSortingTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    private val testUsers =
        listOf(
            UserResponse(
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                email = "a@example.com",
                fullName = "Aaron Smith",
                fullNameNepali = "आरोन स्मिथ",
                wardNumber = 1,
                officePost = "Officer",
                roles = setOf(RoleType.VIEWER),
                status = UserStatus.ACTIVE,
                profilePictureUrl = null,
                createdAt = LocalDateTime.now().minusDays(1),
                updatedAt = LocalDateTime.now(),
                isMunicipalityLevel = false,
            ),
            UserResponse(
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                email = "b@example.com",
                fullName = "Bob Johnson",
                fullNameNepali = "बब जोन्सन",
                wardNumber = 2,
                officePost = "Manager",
                roles = setOf(RoleType.VIEWER),
                status = UserStatus.ACTIVE,
                profilePictureUrl = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                isMunicipalityLevel = false,
            ),
        )

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `sort by FULL_NAME ascending`() {
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers))

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("sortBy", "FULL_NAME")
                    .param("sortDirection", "ASC"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].fullName").value("Aaron Smith"))
            .andExpect(jsonPath("$.data[1].fullName").value("Bob Johnson"))
            .andExpect(jsonPath("$.meta.total").value(2))
            .andExpect(jsonPath("$.message").value("Found 2 users"))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `sort by EMAIL descending`() {
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers.reversed()))

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("sortBy", "EMAIL")
                    .param("sortDirection", "DESC"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].email").value("b@example.com"))
            .andExpect(jsonPath("$.data[1].email").value("a@example.com"))
            .andExpect(jsonPath("$.meta.total").value(2))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `sort by WARD_NUMBER ascending`() {
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers))

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("sortBy", "WARD_NUMBER")
                    .param("sortDirection", "ASC"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].wardNumber").value(1))
            .andExpect(jsonPath("$.data[1].wardNumber").value(2))
            .andExpect(jsonPath("$.meta.page").value(1))
            .andExpect(jsonPath("$.meta.size").value(2))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `sort by CREATED_AT descending`() {
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers.reversed()))

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("sortBy", "CREATED_AT")
                    .param("sortDirection", "DESC"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("123e4567-e89b-12d3-a456-426614174001"))
            .andExpect(jsonPath("$.data[1].id").value("123e4567-e89b-12d3-a456-426614174000"))
            .andExpect(jsonPath("$.meta.hasMore").value(false))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `invalid sort field should use default sort`() {
        // Arrange
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers))

        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("sortBy", "INVALID_FIELD"),
            ).andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `invalid sort direction should use default direction`() {
        // Arrange
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers))

        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("sortBy", "CREATED_AT")
                    .param("sortDirection", "INVALID"),
            ).andExpect(status().isBadRequest)
    }
}
