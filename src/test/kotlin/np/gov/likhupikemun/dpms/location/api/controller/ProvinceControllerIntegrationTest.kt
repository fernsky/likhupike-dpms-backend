package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSearchCriteria
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Province Controller Integration Tests")
class ProvinceControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @Autowired
    private lateinit var provinceService: ProvinceService

    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var municipalityService: MunicipalityService

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
            // Given
            val request = ProvinceTestFixtures.createProvinceRequest()

            // Act & Assert
            mockMvc.perform(
                post("/api/v1/provinces")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(request.name))
                .andExpect(jsonPath("$.data.code").value(request.code))
                .andExpect(jsonPath("$.data.isActive").value(true))
        }

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should return 403 when unauthorized user tries to create province`() {
            // Given
            val request = ProvinceTestFixtures.createProvinceRequest()

            // Act & Assert
            mockMvc.perform(
                post("/api/v1/provinces")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf())
            )
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    @DisplayName("Update Province Tests")
    inner class UpdateProvinceTests {

        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should update province successfully`() {
            // Given
            val province = createTestProvince()
            val updateRequest = ProvinceTestFixtures.createUpdateProvinceRequest()

            // Act & Assert
            mockMvc.perform(
                put("/api/v1/provinces/${province.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
                    .with(csrf())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(updateRequest.name))
                .andExpect(jsonPath("$.data.population").value(updateRequest.population))
        }
    }

    @Nested
    @DisplayName("Search Province Tests")
    inner class SearchProvinceTests {

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should search provinces with filters`() {
            // Given
            createTestProvinces()

            // Act & Assert
            mockMvc.perform(
                get("/api/v1/provinces/search")
                    .param("minPopulation", "400000")
                    .param("searchTerm", "Test")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.totalElements").isNumber)
                .andExpect(jsonPath("$.data.content[0].population").exists())
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    inner class StatisticsTests {

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get province statistics`() {
            // Given
            val province = createTestProvince()
            val district = createTestDistrict(province.id)
            createTestMunicipalities(district.id)

            // Act & Assert
            mockMvc.perform(get("/api/v1/provinces/${province.id}/statistics"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.totalDistricts").value(1))
                .andExpect(jsonPath("$.data.totalMunicipalities").exists())
                .andExpect(jsonPath("$.data.totalPopulation").exists())
                .andExpect(jsonPath("$.data.municipalityTypes").exists())
        }
    }

    @Nested
    @DisplayName("Large Province Search Tests")
    inner class LargeProvinceSearchTests {

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should find large provinces`() {
            // Given
            createLargeProvinces()

            // Act & Assert
            mockMvc.perform(
                get("/api/v1/provinces/large")
                    .param("minArea", "5000.00")
                    .param("minPopulation", "500000")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.content[0].area").exists())
                .andExpect(jsonPath("$.data.content[0].population").exists())
        }
    }

    private fun createTestProvince() = provinceService.createProvince(
        ProvinceTestFixtures.createProvinceRequest()
    )

    private fun createTestDistrict(provinceId: UUID) = districtService.createDistrict(
        DistrictTestFixtures.createDistrictRequest(provinceId = provinceId)
    )

    private fun createTestMunicipalities(districtId: UUID) {
        repeat(3) { i ->
            municipalityService.createMunicipality(
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtId = districtId,
                    code = "TEST-M$i"
                )
            )
        }
    }

    private fun createTestProvinces() {
        repeat(5) { i ->
            provinceService.createProvince(
                ProvinceTestFixtures.createProvinceRequest(
                    code = "TEST-P$i",
                    name = "Test Province $i",
                    population = 400000L + (i * 50000L)
                )
            )
        }
    }

    private fun createLargeProvinces() {
        repeat(3) { i ->
            provinceService.createProvince(
                ProvinceTestFixtures.createProvinceRequest(
                    code = "LARGE-P$i",
                    area = BigDecimal("5000.00").add(BigDecimal(i * 1000)),
                    population = 500000L + (i * 100000L)
                )
            )
        }
    }
}
