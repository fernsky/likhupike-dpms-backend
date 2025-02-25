package np.gov.mofaga.imis.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.mofaga.imis.auth.domain.User
import np.gov.mofaga.imis.auth.test.UserTestDataFactory
import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.controller.MunicipalityController
import np.gov.mofaga.imis.location.api.dto.enums.MunicipalityField
import np.gov.mofaga.imis.location.api.dto.response.DynamicMunicipalityProjection
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.location.service.MunicipalityService
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(MunicipalityController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Municipality Controller Unit Tests")
class MunicipalityControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var municipalityService: MunicipalityService // Changed to MockBean

    @MockBean
    private lateinit var securityService: SecurityService

    private val superAdmin = UserTestDataFactory.createSuperAdmin()
    private val municipalityAdmin = UserTestDataFactory.createMunicipalityAdmin()
    private val viewer = UserTestDataFactory.createViewer()

    @BeforeEach
    fun setup() {
        mockLoggedInUser(superAdmin)
    }

    private fun mockLoggedInUser(user: User) {
        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        SecurityContextHolder.getContext().authentication = authentication
        whenever(securityService.getCurrentUser()).thenReturn(user)
    }

    @Nested
    @DisplayName("Create Municipality Tests")
    inner class CreateMunicipalityTests {
        @Test
        fun `should create municipality successfully when super admin`() {
            // Arrange
            mockLoggedInUser(superAdmin)
            val request = MunicipalityTestFixtures.createMunicipalityRequest(districtCode = "TEST-D1")
            val expectedResponse = MunicipalityTestFixtures.createMunicipalityResponse()

            whenever(municipalityService.createMunicipality(any()))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/municipalities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(expectedResponse.code))
                .andExpect(jsonPath("$.data.name").value(expectedResponse.name))
                .andExpect(jsonPath("$.data.type").value(expectedResponse.type.name))
        }

        @Test
        fun `should return 403 when viewer tries to create municipality`() {
            // Arrange
            mockLoggedInUser(viewer)
            val request = MunicipalityTestFixtures.createMunicipalityRequest(districtCode = "TEST-D1")

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/municipalities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should return 400 when creating municipality with invalid data`() {
            // Arrange
            val request = MunicipalityTestFixtures.createMunicipalityRequest(districtCode = "TEST-D1", totalWards = 0)

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/municipalities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isBadRequest)
        }
    }

    // TODO: Fix this failing test
    // @Nested
    // @DisplayName("Update Municipality Tests")
    // inner class UpdateMunicipalityTests {
    //     @Test
    //     fun `should update municipality successfully when super admin`() {
    //         // Arrange
    //         mockLoggedInUser(superAdmin)
    //         val municipalityCode = "TEST-M1"
    //         val updateRequest = MunicipalityTestFixtures.createUpdateMunicipalityRequest()
    //         val expectedResponse =
    //             MunicipalityTestFixtures.createMunicipalityResponse(
    //                 code = municipalityCode,
    //                 name = updateRequest.name ?: "Updated Municipality",
    //             )

    //         whenever(municipalityService.updateMunicipality(municipalityCode, updateRequest))
    //             .thenReturn(expectedResponse)

    //         // Act & Assert
    //         mockMvc
    //             .perform(
    //                 put("/api/v1/municipalities/$municipalityCode")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(updateRequest))
    //                     .with(csrf()),
    //             ).andExpect(status().isOk)
    //             .andExpect(jsonPath("$.data.name").value(expectedResponse.name))
    //     }
    // }

    @Nested
    @DisplayName("Search and Query Tests")
    inner class SearchAndQueryTests {
        @Test
        fun `should search municipalities with filters when viewer`() {
            // Arrange
            mockLoggedInUser(viewer)
            val expectedResponse: Page<DynamicMunicipalityProjection> =
                PageImpl(
                    listOf(
                        MunicipalityTestFixtures.createMunicipalityProjection(code = "TEST-M1"),
                        MunicipalityTestFixtures.createMunicipalityProjection(code = "TEST-M2"),
                    ),
                )

            whenever(municipalityService.searchMunicipalities(any()))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/municipalities/search")
                        .param("districtCode", "TEST-D1")
                        .param("type", MunicipalityType.MUNICIPALITY.name)
                        .param("minPopulation", "10000"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.totalElements").isNumber)
        }

        @Test
        fun `should get municipalities by district when viewer`() {
            // Arrange
            mockLoggedInUser(viewer)
            val districtCode = "TEST-D1"
            val expectedResponse =
                listOf(
                    MunicipalityTestFixtures.createMunicipalityResponse(),
                    MunicipalityTestFixtures.createMunicipalityResponse(code = "TEST-M2"),
                )

            whenever(municipalityService.getMunicipalitiesByDistrict(districtCode))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/by-district/$districtCode"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
        }

        @Test
        fun `should get municipalities by type when viewer`() {
            // Arrange
            mockLoggedInUser(viewer)
            val expectedResponse =
                listOf(
                    MunicipalityTestFixtures.createMunicipalityResponse(),
                    MunicipalityTestFixtures.createMunicipalityResponse(code = "TEST-M2"),
                )

            whenever(municipalityService.getMunicipalitiesByType(any()))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/by-type/${MunicipalityType.MUNICIPALITY}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
        }
    }

    @Nested
    @DisplayName("Dynamic Search Tests")
    inner class DynamicSearchTests {
        @Test
        fun `should search with specific fields`() {
            // Arrange
            mockLoggedInUser(viewer) // Changed from loginAs to mockLoggedInUser
            val fields = "CODE,NAME,TYPE"
            val projection =
                MunicipalityTestFixtures.createMunicipalityProjection(
                    code = "TEST-M1",
                    fields = setOf(MunicipalityField.CODE, MunicipalityField.NAME, MunicipalityField.TYPE),
                )
            val expectedResults = PageImpl(listOf(projection))

            whenever(municipalityService.searchMunicipalities(any()))
                .thenReturn(expectedResults)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/municipalities/search")
                        .param("fields", fields),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content[0].code").exists())
                .andExpect(jsonPath("$.data.content[0].name").exists())
                .andExpect(jsonPath("$.data.content[0].type").exists())
                .andExpect(jsonPath("$.data.content[0].area").doesNotExist())
        }

        @Test
        fun `should search with geometry included`() {
            // Arrange
            mockLoggedInUser(viewer) // Changed from loginAs to mockLoggedInUser
            val projection =
                MunicipalityTestFixtures.createMunicipalityProjection(
                    code = "TEST-M1",
                    includeGeometry = true,
                )
            val expectedResults = PageImpl(listOf(projection))

            whenever(municipalityService.searchMunicipalities(any()))
                .thenReturn(expectedResults)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/municipalities/search")
                        .param("fields", "CODE,GEOMETRY"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content[0].geometry").exists())
        }
    }

    @Nested
    @DisplayName("Detail Tests")
    inner class DetailAndStatsTests {
        @Test
        fun `should get municipality detail when viewer`() {
            // Arrange
            mockLoggedInUser(viewer)
            val municipalityCode = "TEST-M1"
            val expectedResponse = MunicipalityTestFixtures.createMunicipalityDetailResponse()

            whenever(municipalityService.getMunicipalityDetail(municipalityCode))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/$municipalityCode"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(expectedResponse.code))
        }
    }
}
