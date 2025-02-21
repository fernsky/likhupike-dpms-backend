package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.config.TestSecurityConfig
import np.gov.likhupikemun.dpms.location.api.controller.ProvinceController
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.service.DistrictService
import np.gov.likhupikemun.dpms.location.service.MunicipalityService
import np.gov.likhupikemun.dpms.location.service.ProvinceService
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@WebMvcTest(ProvinceController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Province Controller Integration Tests")
class ProvinceControllerIntegrationTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Autowired private lateinit var provinceRepository: ProvinceRepository

    @Autowired private lateinit var provinceService: ProvinceService

    @Autowired private lateinit var districtService: DistrictService

    @Autowired private lateinit var municipalityService: MunicipalityService

    @BeforeEach
    fun setup() {
        provinceRepository.deleteAll()
    }

    @Nested
    @DisplayName("Create Province Tests")
    inner class CreateProvinceTests {
        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should create province successfully`() {
            val request = ProvinceTestFixtures.createProvinceRequest()

            mockMvc
                .perform(
                    post("/api/v1/provinces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(request.name))
                .andExpect(jsonPath("$.data.code").value(request.code))
                .andExpect(jsonPath("$.data.districtCount").value(0))
        }

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should return 403 when unauthorized user tries to create province`() {
            val request = ProvinceTestFixtures.createProvinceRequest()

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
        @WithMockUser(roles = ["VIEWER"])
        fun `should get province detail successfully`() {
            val province = createTestProvince()

            mockMvc
                .perform(get("/api/v1/provinces/${province.code}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(province.code))
                .andExpect(jsonPath("$.data.name").value(province.name))
                .andExpect(jsonPath("$.data.districts").exists())
        }
    }

    @Nested
    @DisplayName("Search Province Tests")
    inner class SearchProvinceTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should search provinces with criteria`() {
            createTestProvinces()

            mockMvc
                .perform(
                    get("/api/v1/provinces/search")
                        .param("searchTerm", "Test")
                        .param("page", "0")
                        .param("pageSize", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.totalElements").isNumber)
        }
    }

    @Nested
    @DisplayName("Update Province Tests")
    inner class UpdateProvinceTests {
        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should update province successfully`() {
            val province = createTestProvince()
            val updateRequest = ProvinceTestFixtures.createUpdateProvinceRequest()

            mockMvc
                .perform(
                    put("/api/v1/provinces/${province.code}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(updateRequest.name))
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    inner class StatisticsTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get province statistics`() {
            val province = createTestProvince()
            val district = createTestDistrict(province.code)
            createTestMunicipalities(district.code)

            mockMvc
                .perform(get("/api/v1/provinces/${province.code}/statistics"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.totalDistricts").exists())
                .andExpect(jsonPath("$.data.totalMunicipalities").exists())
                .andExpect(jsonPath("$.data.totalPopulation").exists())
        }
    }

    @Nested
    @DisplayName("Large Province Tests")
    inner class LargeProvinceTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should find large provinces`() {
            createLargeProvinces()

            mockMvc
                .perform(
                    get("/api/v1/provinces/large")
                        .param("minArea", "5000.00")
                        .param("minPopulation", "500000")
                        .param("page", "0")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.content[0].area").exists())
        }
    }

    // Helper methods
    private fun createTestProvince() =
        provinceService.createProvince(
            ProvinceTestFixtures.createProvinceRequest(),
        )

    private fun createTestDistrict(provinceCode: String) =
        districtService.createDistrict(
            DistrictTestFixtures.createDistrictRequest(provinceCode = provinceCode),
        )

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

    private fun createTestProvinces() {
        repeat(3) { i ->
            provinceService.createProvince(
                ProvinceTestFixtures.createProvinceRequest(
                    code = "TEST-P$i",
                    name = "Test Province $i",
                ),
            )
        }
    }

    private fun createLargeProvinces() {
        repeat(2) { i ->
            provinceService.createProvince(
                ProvinceTestFixtures.createProvinceRequest(
                    code = "LARGE-P$i",
                    area = BigDecimal("5000.00").add(BigDecimal(i * 1000)),
                    population = 500000L + (i * 100000L),
                ),
            )
        }
    }
}
