package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.config.TestSecurityConfig
import np.gov.likhupikemun.dpms.location.api.controller.MunicipalityController
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.service.MunicipalityService
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
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

@WebMvcTest(WardController::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Municipality Controller Integration Tests")
class MunicipalityControllerIntegrationTest {
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Autowired private lateinit var districtRepository: DistrictRepository

    @Autowired private lateinit var municipalityService: MunicipalityService

    private lateinit var testDistrict: District

    @BeforeEach
    fun setup() {
        testDistrict = createAndPersistDistrict()
    }

    @Nested
    @DisplayName("Create Municipality Tests")
    inner class CreateMunicipalityTests {
        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should create municipality successfully`() {
            // Arrange
            val request =
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = testDistrict.code!!,
                )

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/municipalities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(request.code))
                .andExpect(jsonPath("$.data.name").value(request.name))
                .andExpect(jsonPath("$.data.type").value(request.type.name))
        }

        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should return 400 when creating municipality with invalid data`() {
            // Arrange
            val request =
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = testDistrict.code!!,
                    totalWards = 0, // Invalid ward count
                )

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

    @Nested
    @DisplayName("Update Municipality Tests")
    inner class UpdateMunicipalityTests {
        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should update municipality successfully`() {
            // Arrange
            val municipality = createTestMunicipality()
            val updateRequest = MunicipalityTestFixtures.createUpdateMunicipalityRequest()

            // Act & Assert
            mockMvc
                .perform(
                    put("/api/v1/municipalities/${municipality.code}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(updateRequest.name))
                .andExpect(jsonPath("$.data.population").value(updateRequest.population))
        }
    }

    @Nested
    @DisplayName("Search and Query Tests")
    inner class SearchAndQueryTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should search municipalities with filters`() {
            // Arrange
            createTestMunicipalities()

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/municipalities/search")
                        .param("districtCode", testDistrict.code)
                        .param("type", MunicipalityType.MUNICIPALITY.name)
                        .param("minPopulation", "10000"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.totalElements").isNumber)
        }

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get municipalities by district`() {
            // Arrange
            createTestMunicipality()

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/by-district/${testDistrict.code}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
        }

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get municipalities by type`() {
            // Arrange
            createTestMunicipality()

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/by-type/${MunicipalityType.MUNICIPALITY}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
        }
    }

    @Nested
    @DisplayName("Detail and Statistics Tests")
    inner class DetailAndStatsTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get municipality detail`() {
            // Arrange
            val municipality = createTestMunicipality()

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/${municipality.code}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(municipality.code))
                .andExpect(jsonPath("$.data.district").exists())
        }

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get municipality statistics`() {
            // Arrange
            val municipality = createTestMunicipality()

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/${municipality.code}/statistics"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.totalWards").exists())
                .andExpect(jsonPath("$.data.totalPopulation").exists())
        }
    }

    // Helper methods
    private fun createAndPersistDistrict(): District = districtRepository.save(DistrictTestFixtures.createDistrict())

    private fun createTestMunicipality() =
        municipalityService.createMunicipality(
            MunicipalityTestFixtures.createMunicipalityRequest(
                districtCode = testDistrict.code!!,
            ),
        )

    private fun createTestMunicipalities() {
        repeat(5) { i ->
            municipalityService.createMunicipality(
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = testDistrict.code!!,
                    code = "TEST-M$i",
                    population = 10000L + (i * 1000),
                    type = MunicipalityType.MUNICIPALITY,
                ),
            )
        }
    }

    private fun createTestMunicipalityWithLocation() {
        municipalityService.createMunicipality(
            MunicipalityTestFixtures.createMunicipalityRequest(
                districtCode = testDistrict.code!!,
                code = "GEO-TEST",
                latitude = BigDecimal("27.7172"),
                longitude = BigDecimal("85.3240"),
            ),
        )
    }
}
