package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.service.DistrictService
import np.gov.likhupikemun.dpms.location.service.MunicipalityService
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("District Controller Integration Tests")
class DistrictControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var municipalityService: MunicipalityService

    private lateinit var testProvince: Province

    @BeforeEach
    fun setup() {
        testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())
    }

    @Nested
    @DisplayName("Create District Tests")
    inner class CreateDistrictTests {
        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should create district successfully`() {
            // Arrange
            val request = DistrictTestFixtures.createDistrictRequest(provinceCode = testProvince.code!!)

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
                .andExpect(jsonPath("$.data.code").value(request.code))
                .andExpect(jsonPath("$.data.province").exists())
        }

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should return 403 when unauthorized user tries to create district`() {
            val request = DistrictTestFixtures.createDistrictRequest(provinceCode = testProvince.code!!)
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
        @WithMockUser(roles = ["VIEWER"])
        fun `should get district detail successfully`() {
            // Arrange
            val district = createTestDistrict()

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/districts/${district.code}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(district.code))
                .andExpect(jsonPath("$.data.name").value(district.name))
                .andExpect(jsonPath("$.data.municipalities").exists())
        }
    }

    @Nested
    @DisplayName("Search District Tests")
    inner class SearchDistrictTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should search districts with criteria`() {
            // Arrange
            createTestDistricts()

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
    @DisplayName("Update District Tests")
    inner class UpdateDistrictTests {
        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should update district successfully`() {
            // Arrange
            val district = createTestDistrict()
            val updateRequest = DistrictTestFixtures.createUpdateDistrictRequest()

            // Act & Assert
            mockMvc
                .perform(
                    put("/api/v1/districts/${district.code}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(updateRequest.name))
        }
    }

    @Nested
    @DisplayName("Province-based District Tests")
    inner class ProvinceBasedDistrictTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get districts by province`() {
            // Arrange
            createTestDistricts()

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/districts/by-province/${testProvince.code}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data[0].province.code").value(testProvince.code))
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    inner class StatisticsTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get district statistics`() {
            // Arrange
            val district = createTestDistrict()
            createTestMunicipalities(district.code)

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/districts/${district.code}/statistics"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.totalMunicipalities").exists())
                .andExpect(jsonPath("$.data.totalPopulation").exists())
                .andExpect(jsonPath("$.data.totalArea").exists())
        }
    }

    private fun createTestDistrict() =
        districtService.createDistrict(
            DistrictTestFixtures.createDistrictRequest(provinceCode = testProvince.code!!),
        )

    private fun createTestDistricts() {
        repeat(3) { i ->
            districtService.createDistrict(
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = testProvince.code!!,
                    code = "TEST-D$i",
                    name = "Test District $i",
                ),
            )
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
