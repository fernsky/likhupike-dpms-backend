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
class UserControllerFilteringTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    private val testUsers =
        listOf(
            UserResponse(
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                email = "ward1@municipality.gov.np",
                fullName = "Ward One User",
                fullNameNepali = "वडा एक प्रयोगकर्ता",
                wardNumber = 1,
                officePost = "Ward Officer",
                roles = setOf(RoleType.WARD_ADMIN),
                status = UserStatus.ACTIVE,
                profilePictureUrl = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                isMunicipalityLevel = false, // Add for ward user
            ),
            UserResponse(
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                email = "admin@municipality.gov.np",
                fullName = "Municipality Admin",
                fullNameNepali = "नगरपालिका एडमिन",
                wardNumber = null,
                officePost = "Chief Officer",
                roles = setOf(RoleType.MUNICIPALITY_ADMIN),
                status = UserStatus.ACTIVE,
                profilePictureUrl = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                isMunicipalityLevel = true, // Add for municipality admin
            ),
        )

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `filter by ward number range`() {
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers.filter { it.wardNumber == 1 }))

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("wardNumberFrom", "1")
                    .param("wardNumberTo", "1"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].wardNumber").value(1))
            .andExpect(jsonPath("$.data.totalElements").value(1))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `filter by role`() {
        whenever(userService.searchUsers(any())).thenReturn(
            PageImpl(testUsers.filter { it.roles.contains(RoleType.WARD_ADMIN) }),
        )

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("roles", "WARD_ADMIN"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].roles[0]").value("WARD_ADMIN"))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `filter by search term`() {
        whenever(userService.searchUsers(any())).thenReturn(
            PageImpl(testUsers.filter { it.fullName.contains("Ward") }),
        )

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("searchTerm", "Ward"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].fullName").value("Ward One User"))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `filter by municipality level`() {
        whenever(userService.searchUsers(any())).thenReturn(
            PageImpl(testUsers.filter { it.wardNumber == null }),
        )

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("isMunicipalityLevel", "true"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].email").value("admin@municipality.gov.np"))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `filter by office post`() {
        whenever(userService.searchUsers(any())).thenReturn(
            PageImpl(testUsers.filter { it.officePost == "Ward Officer" }),
        )

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("officePosts", "Ward Officer"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].officePost").value("Ward Officer"))
    }

    // TODO [TEST] Invalid ward number range should return 400

    // @Test
    // @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    // fun `invalid ward number range should return 400`() {
    //     mockMvc
    //         .perform(
    //             get("/api/v1/users/search")
    //                 .param("wardNumberFrom", "5")
    //                 .param("wardNumberTo", "1"),
    //         ).andExpect(status().isBadRequest)
    // }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `invalid page number should return 400`() {
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("page", "-1"),
            ).andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `invalid page size should return 400`() {
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("pageSize", "0"),
            ).andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `combine multiple filters`() {
        whenever(userService.searchUsers(any())).thenReturn(
            PageImpl(
                testUsers.filter {
                    it.wardNumber == 1 &&
                        it.roles.contains(RoleType.WARD_ADMIN) &&
                        it.officePost == "Ward Officer"
                },
            ),
        )

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("wardNumberFrom", "1")
                    .param("wardNumberTo", "1")
                    .param("roles", "WARD_ADMIN")
                    .param("officePosts", "Ward Officer"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content[0].id").value("123e4567-e89b-12d3-a456-426614174000"))
            .andExpect(jsonPath("$.data.totalElements").value(1))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `invalid ward number too high should return 400`() {
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("wardNumberFrom", "6"),
            ).andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `invalid ward number too low should return 400`() {
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("wardNumberFrom", "0"),
            ).andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `valid ward number range should return 200`() {
        whenever(userService.searchUsers(any())).thenReturn(PageImpl(testUsers))

        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("wardNumberFrom", "1")
                    .param("wardNumberTo", "5"),
            ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `ward number range with invalid to value should return 400`() {
        mockMvc
            .perform(
                get("/api/v1/users/search")
                    .param("wardNumberFrom", "1")
                    .param("wardNumberTo", "6"),
            ).andExpect(status().isBadRequest)
    }
}
