package np.gov.mofaga.imis.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.mofaga.imis.auth.domain.User
import np.gov.mofaga.imis.auth.test.UserTestDataFactory
import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.controller.WardController
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.location.service.WardService
import np.gov.mofaga.imis.location.test.fixtures.WardTestFixtures
import np.gov.mofaga.imis.shared.service.SecurityService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@WebMvcTest(WardController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
class WardControllerTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var wardService: WardService

    @MockBean private lateinit var securityService: SecurityService

    private lateinit var testMunicipality: Municipality

    private val superAdmin = UserTestDataFactory.createSuperAdmin()
    private val municipalityAdmin = UserTestDataFactory.createMunicipalityAdmin()
    private val viewer = UserTestDataFactory.createViewer()

    @BeforeEach
    fun setup() {
        testMunicipality = createTestMunicipality()
    }

    private fun mockLoggedInUser(user: User) {
        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        SecurityContextHolder.getContext().authentication = authentication
        whenever(securityService.getCurrentUser()).thenReturn(user)
    }

    @Test
    fun `should create ward successfully when super admin`() {
        // Arrange
        mockLoggedInUser(superAdmin)
        val request = WardTestFixtures.createWardRequest()
        val response = WardTestFixtures.createWardResponse()
        whenever(wardService.createWard(any())).thenReturn(response)

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/wards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(response.wardNumber))
            .andExpect(jsonPath("$.data.municipality.code").value(response.municipality.code))
    }

    // TODO: Fix this failing test
    // @Test
    // fun `should update ward successfully when municipality admin`() {
    //     // Arrange
    //     mockLoggedInUser(municipalityAdmin)
    //     val updateRequest = WardTestFixtures.createUpdateWardRequest()
    //     val response = WardTestFixtures.createWardResponse()
    //     whenever(wardService.updateWard(1, "TEST-M", updateRequest)).thenReturn(response)

    //     // Act & Assert
    //     mockMvc
    //         .perform(
    //             put("/api/v1/wards/TEST-M/1")
    //                 .contentType(MediaType.APPLICATION_JSON)
    //                 .content(objectMapper.writeValueAsString(updateRequest))
    //                 .with(csrf()),
    //         ).andExpect(status().isOk)
    //         .andExpect(jsonPath("$.data.wardNumber").value(response.wardNumber))
    // }

    @Test
    fun `should get ward detail successfully when viewer`() {
        // Arrange
        mockLoggedInUser(viewer)
        val response = WardTestFixtures.createWardDetailResponse()
        whenever(wardService.getWardDetail(1, "TEST-M")).thenReturn(response)

        // Act & Assert
        mockMvc
            .perform(get("/api/v1/wards/TEST-M/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(1))
    }

    @Test
    fun `should return 403 when viewer tries to create ward`() {
        // Arrange
        mockLoggedInUser(viewer)
        val request = WardTestFixtures.createWardRequest()

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/wards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()),
            ).andExpect(status().isForbidden)
    }

    @Nested
    @DisplayName("Dynamic Search Tests")
    inner class DynamicSearchTests {
        @Test
        fun `should search with specific fields`() {
            // Arrange
            loginAs(viewer)
            val fields = "WARD_NUMBER,POPULATION,MUNICIPALITY"
            val projection =
                WardTestFixtures.createWardProjection(
                    wardNumber = 1,
                    fields = setOf(WardField.WARD_NUMBER, WardField.POPULATION, WardField.MUNICIPALITY),
                )
            val expectedResults = PageImpl(listOf(projection))

            whenever(wardService.searchWards(any()))
                .thenReturn(expectedResults)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/wards/search")
                        .param("fields", fields),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content[0].wardNumber").exists())
                .andExpect(jsonPath("$.data.content[0].population").exists())
                .andExpect(jsonPath("$.data.content[0].municipality").exists())
                .andExpect(jsonPath("$.data.content[0].area").doesNotExist())
        }

        @Test
        fun `should search with geometry included`() {
            // Arrange
            loginAs(viewer)
            val projection =
                WardTestFixtures.createWardProjection(
                    wardNumber = 1,
                    includeGeometry = true,
                )
            val expectedResults = PageImpl(listOf(projection))

            whenever(wardService.searchWards(any()))
                .thenReturn(expectedResults)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/wards/search")
                        .param("fields", "WARD_NUMBER,GEOMETRY"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content[0].geometry").exists())
        }
    }

    // Helper methods
    private fun createTestMunicipality() =
        Municipality().apply {
            name = "Test Municipality"
            nameNepali = "परीक्षण नगरपालिका"
            code = "TEST-M"
            type = MunicipalityType.MUNICIPALITY
            area = BigDecimal("100.00")
            population = 10000L
            totalWards = 10
        }
}
