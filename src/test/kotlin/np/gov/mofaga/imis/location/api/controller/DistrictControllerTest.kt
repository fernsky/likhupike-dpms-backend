package np.gov.mofaga.imis.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.mofaga.imis.auth.domain.User
import np.gov.mofaga.imis.auth.test.UserTestDataFactory
import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.controller.DistrictController
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.service.DistrictService
import np.gov.mofaga.imis.location.service.MunicipalityService
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
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
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(DistrictController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("District Controller Unit Tests")
class DistrictControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @MockBean
    private lateinit var districtService: DistrictService // Make sure this is mocked

    @Autowired
    private lateinit var municipalityService: MunicipalityService

    @MockBean
    private lateinit var securityService: SecurityService

    private lateinit var testProvince: Province

    private val superAdmin = UserTestDataFactory.createSuperAdmin()
    private val municipalityAdmin = UserTestDataFactory.createMunicipalityAdmin()
    private val viewer = UserTestDataFactory.createViewer()

    @BeforeEach
    fun setup() {
        // Use ProvinceTestFixtures's automatic unique code generation
        testProvince =
            provinceRepository.save(
                ProvinceTestFixtures.createProvince(
                    name = "Test Province for District Controller Test",
                ),
            )
    }

    private fun mockLoggedInUser(user: User) {
        val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        SecurityContextHolder.getContext().authentication = authentication
        whenever(securityService.getCurrentUser()).thenReturn(user)
    }

    @Nested
    @DisplayName("Create District Tests")
    inner class CreateDistrictTests {
        @Test
        fun `should create district successfully when super admin`() {
            // Arrange
            mockLoggedInUser(superAdmin)
            val uniqueDistrictCode = "TEST-D-${System.currentTimeMillis()}-${(1000..9999).random()}"
            val request =
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = testProvince.code!!,
                    code = uniqueDistrictCode,
                    name = "Test District $uniqueDistrictCode",
                )
            val expectedResponse =
                DistrictTestFixtures.createDistrictResponse(
                    code = uniqueDistrictCode,
                    name = request.name,
                    province =
                        ProvinceTestFixtures.createProvinceSummaryResponse(
                            code = testProvince.code!!,
                            name = testProvince.name!!,
                        ),
                )

            whenever(districtService.createDistrict(any())).thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/districts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(request.name))
                .andExpect(jsonPath("$.data.code").value(uniqueDistrictCode))
                .andExpect(jsonPath("$.data.province.code").value(testProvince.code))
        }

        @Test
        fun `should return 403 when viewer tries to create district`() {
            // Arrange
            mockLoggedInUser(viewer)
            val request = DistrictTestFixtures.createDistrictRequest(provinceCode = testProvince.code!!)

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/districts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isForbidden)
        }
    }

    @Nested
    @DisplayName("Get District Tests")
    inner class GetDistrictTests {
        @Test
        fun `should get district detail successfully`() {
            // Arrange
            mockLoggedInUser(viewer)
            val districtCode = "TEST-D1"
            val expectedResponse =
                DistrictTestFixtures.createDistrictDetailResponse(
                    code = districtCode,
                    provinceCode = testProvince.code!!,
                )

            whenever(districtService.getDistrictDetail(districtCode))
                .thenReturn(expectedResponse)

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/districts/$districtCode"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(districtCode))
                .andExpect(jsonPath("$.data.name").value(expectedResponse.name))
                .andExpect(jsonPath("$.data.municipalities").exists())
        }
    }

    @Nested
    @DisplayName("Search District Tests")
    inner class SearchDistrictTests {
        @Test
        fun `should search districts with criteria`() {
            // Arrange
            mockLoggedInUser(viewer)
            val expectedResults =
                PageImpl(
                    listOf(
                        DistrictTestFixtures.createDistrictResponse(
                            code = "TEST-D1",
                            name = "Test District 1",
                            province = ProvinceTestFixtures.createProvinceSummaryResponse(code = testProvince.code!!),
                        ),
                    ),
                )

            whenever(districtService.searchDistricts(any()))
                .thenReturn(expectedResults)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/districts/search")
                        .param("provinceCode", testProvince.code)
                        .param("searchTerm", "Test")
                        .param("page", "0")
                        .param("pageSize", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.totalElements").isNumber)
        }
    }

    @Nested
    @DisplayName("Dynamic Search Tests")
    inner class DynamicSearchTests {
        @Test
        fun `should search with specific fields`() {
            // Arrange
            loginAs(viewer)
            val fields = "CODE,NAME,POPULATION"
            val projection =
                DistrictTestFixtures.createDistrictProjection(
                    code = "TEST-D1",
                    fields = setOf(DistrictField.CODE, DistrictField.NAME, DistrictField.POPULATION),
                )
            val expectedResults = PageImpl(listOf(projection))

            whenever(districtService.searchDistricts(any()))
                .thenReturn(expectedResults)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/districts/search")
                        .param("fields", fields),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content[0].code").exists())
                .andExpect(jsonPath("$.data.content[0].name").exists())
                .andExpect(jsonPath("$.data.content[0].population").exists())
                .andExpect(jsonPath("$.data.content[0].area").doesNotExist())
        }

        @Test
        fun `should search with municipalities included`() {
            // Arrange
            loginAs(viewer)
            val projection =
                DistrictTestFixtures.createDistrictProjection(
                    code = "TEST-D1",
                    includeMunicipalities = true,
                )
            val expectedResults = PageImpl(listOf(projection))

            whenever(districtService.searchDistricts(any()))
                .thenReturn(expectedResults)

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/districts/search")
                        .param("fields", "CODE,MUNICIPALITIES"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content[0].municipalities").exists())
        }
    }

    // TODO: Fix this failing test
    // @Nested
    // @DisplayName("Update District Tests")
    // inner class UpdateDistrictTests {
    //     @Test
    //     fun `should update district successfully`() {
    //         // Arrange
    //         mockLoggedInUser(superAdmin)
    //         val districtCode = "TEST-D1"
    //         val updateRequest = DistrictTestFixtures.createUpdateDistrictRequest()
    //         val expectedResponse =
    //             DistrictTestFixtures.createDistrictResponse(
    //                 code = districtCode,
    //                 name = updateRequest.name ?: "Updated District",
    //                 province = ProvinceTestFixtures.createProvinceSummaryResponse(code = testProvince.code!!),
    //             )

    //         whenever(districtService.updateDistrict(districtCode, updateRequest))
    //             .thenReturn(expectedResponse)

    //         // Act & Assert
    //         mockMvc
    //             .perform(
    //                 put("/api/v1/districts/$districtCode")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(updateRequest))
    //                     .with(csrf()),
    //             ).andExpect(status().isOk)
    //             .andExpect(jsonPath("$.data.code").value(districtCode))
    //             .andExpect(jsonPath("$.data.name").value(expectedResponse.name))
    //             .andExpect(jsonPath("$.data.province.code").value(testProvince.code))
    //     }
    // }

    @Nested
    @DisplayName("Province-based District Tests")
    inner class ProvinceBasedDistrictTests {
        @Test
        fun `should get districts by province`() {
            // Arrange
            mockLoggedInUser(viewer)
            val provinceCode = testProvince.code!!
            val expectedDistricts =
                listOf(
                    DistrictTestFixtures.createDistrictResponse(
                        code = "TEST-D1-${System.currentTimeMillis().toString().take(2)}",
                        name = "Test District 1",
                        province = ProvinceTestFixtures.createProvinceSummaryResponse(code = provinceCode),
                    ),
                    DistrictTestFixtures.createDistrictResponse(
                        code = "TEST-D2-${System.currentTimeMillis().toString().take(2)}",
                        name = "Test District 2",
                        province = ProvinceTestFixtures.createProvinceSummaryResponse(code = provinceCode),
                    ),
                )

            whenever(districtService.getDistrictsByProvince(provinceCode))
                .thenReturn(expectedDistricts)

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/districts/by-province/$provinceCode"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data[0].province.code").value(provinceCode))
        }
    }

    private fun createTestMunicipalities(districtCode: String) {
        repeat(3) { i ->
            municipalityService.createMunicipality(
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = districtCode,
                    code = "TEST-M$i",
                ),
            )
        }
    }
}
