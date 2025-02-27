package np.gov.mofaga.imis.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.mofaga.imis.auth.domain.User
import np.gov.mofaga.imis.auth.test.UserTestDataFactory
import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.controller.ProvinceController
import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.api.dto.response.DynamicProvinceProjection
import np.gov.mofaga.imis.location.service.ProvinceService
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import np.gov.mofaga.imis.shared.service.SecurityService
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@WebMvcTest(ProvinceController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Province Controller Unit Tests")
class ProvinceControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var provinceService: ProvinceService

    @MockBean
    private lateinit var securityService: SecurityService

    @MockBean
    private lateinit var geometryConverter: GeometryConverter

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
    @DisplayName("Create Province Tests")
    inner class CreateProvinceTests {
        // TODO: Fix this failing test issue
        // @Test
        // fun `should create province successfully when super admin`() {
        //     // Arrange
        //     mockLoggedInUser(superAdmin)
        //     val request = ProvinceTestFixtures.createProvinceRequest()
        //     val expectedResponse =
        //         ProvinceTestFixtures.createProvinceResponse(
        //             code = request.code,
        //             name = request.name,
        //         )

        //     whenever(provinceService.createProvince(request)).thenReturn(expectedResponse)

        //     // Act & Assert
        //     mockMvc
        //         .perform(
        //             post("/api/v1/provinces")
        //                 .contentType(MediaType.APPLICATION_JSON)
        //                 .content(objectMapper.writeValueAsString(request))
        //                 .with(csrf()),
        //         ).andExpect(status().isOk)
        //         .andExpect(jsonPath("$.data.name").value(request.name))
        //         .andExpect(jsonPath("$.data.code").value(request.code))
        // }

        @Test
        fun `should return 403 when viewer tries to create province`() {
            // Arrange
            mockLoggedInUser(viewer)
            val request = ProvinceTestFixtures.createProvinceRequest()

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isForbidden)
        }
    }

    @Nested
    @DisplayName("Get Province Tests")
    inner class GetProvinceTests {
        @Test
        fun `should get province detail successfully`() {
            // Arrange
            mockLoggedInUser(viewer)
            val provinceCode = "TEST-P1"
            val expectedResponse =
                ProvinceTestFixtures.createProvinceDetailResponse(
                    code = provinceCode, // Explicitly set the code to match what we expect
                )

            whenever(provinceService.getProvinceDetail(provinceCode))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/provinces/$provinceCode"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(expectedResponse.code)) // Use the response code
                .andExpect(jsonPath("$.data.districts").exists())
        }
    }

    @Nested
    @DisplayName("Search Province Tests")
    inner class SearchProvinceTests {
        @Test
        fun `should search provinces with criteria`() {
            mockLoggedInUser(viewer)
            val criteria =
                ProvinceSearchCriteria(
                    searchTerm = "Test",
                    page = 0,
                    pageSize = 10,
                )

            val expectedResults =
                PageImpl(
                    listOf(
                        DynamicProvinceProjection.from(
                            ProvinceTestFixtures.createProvince(code = "TEST-P1"),
                            ProvinceField.DEFAULT_FIELDS,
                            geometryConverter,
                        ),
                        DynamicProvinceProjection.from(
                            ProvinceTestFixtures.createProvince(code = "TEST-P2"),
                            ProvinceField.DEFAULT_FIELDS,
                            geometryConverter,
                        ),
                    ),
                    PageRequest.of(0, 10),
                    2,
                )

            whenever(provinceService.searchProvinces(any()))
                .thenReturn(expectedResults)

            mockMvc
                .perform(
                    get("/api/v1/provinces/search")
                        .param("searchTerm", criteria.searchTerm)
                        .param("page", criteria.page.toString())
                        .param("pageSize", criteria.pageSize.toString()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.meta.total").value(2))
                .andExpect(jsonPath("$.meta.page").value(1))
                .andExpect(jsonPath("$.meta.size").value(10))
                .andExpect(jsonPath("$.message").value("Found 2 provinces"))
        }

        @Test
        fun `should search provinces with specific fields`() {
            mockLoggedInUser(viewer)
            val fields = "CODE,NAME"
            val expectedResults =
                PageImpl(
                    listOf(
                        ProvinceTestFixtures.createProvinceProjection(
                            code = "TEST-P1",
                            fields = setOf(ProvinceField.CODE, ProvinceField.NAME),
                        ),
                    ),
                    PageRequest.of(0, 10),
                    1,
                )

            whenever(provinceService.searchProvinces(any()))
                .thenReturn(expectedResults)

            mockMvc
                .perform(
                    get("/api/v1/provinces/search")
                        .param("fields", fields),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].code").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].nameNepali").doesNotExist())
                .andExpect(jsonPath("$.data[0].area").doesNotExist())
                .andExpect(jsonPath("$.meta.total").value(1))
                .andExpect(jsonPath("$.message").value("Found 1 provinces"))
        }
    }

    @Nested
    @DisplayName("Update Province Tests")
    inner class UpdateProvinceTests {
        @Test
        fun `should update province successfully`() {
            // Arrange
            mockLoggedInUser(superAdmin)
            val provinceCode = "TEST-P1"
            val updateRequest = ProvinceTestFixtures.createUpdateProvinceRequest()
            val expectedResponse =
                ProvinceTestFixtures.createProvinceResponse(
                    code = provinceCode,
                    name = updateRequest.name!!,
                )

            whenever(provinceService.updateProvince(eq(provinceCode), any()))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(
                    put("/api/v1/provinces/$provinceCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(updateRequest.name))
        }

        @Test
        fun `should return 403 when viewer tries to update province`() {
            // Arrange
            mockLoggedInUser(viewer)
            val provinceCode = "TEST-P1"
            val updateRequest = ProvinceTestFixtures.createUpdateProvinceRequest()

            // Act & Assert
            mockMvc
                .perform(
                    put("/api/v1/provinces/$provinceCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()),
                ).andExpect(status().isForbidden)
        }
    }

    @Nested
    @DisplayName("Large Province Tests")
    inner class LargeProvinceTests {
        @Test
        fun `should find large provinces`() {
            mockLoggedInUser(viewer)
            val minArea = BigDecimal("5000.00")
            val minPopulation = 500000L
            val expectedResults =
                PageImpl(
                    listOf(
                        ProvinceTestFixtures.createProvinceResponse(
                            code = "LARGE-P1",
                            area = minArea,
                            population = minPopulation,
                        ),
                    ),
                    PageRequest.of(0, 10),
                    1,
                )

            whenever(provinceService.findLargeProvinces(minArea, minPopulation, 0, 10))
                .thenReturn(expectedResults)

            mockMvc
                .perform(
                    get("/api/v1/provinces/large")
                        .param("minArea", minArea.toString())
                        .param("minPopulation", minPopulation.toString())
                        .param("page", "0")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data[0].area").exists())
                .andExpect(jsonPath("$.meta.total").value(1))
                .andExpect(jsonPath("$.meta.size").value(10))
        }
    }
}
