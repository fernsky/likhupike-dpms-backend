package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
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
@DisplayName("Municipality Controller Integration Tests")
class MunicipalityControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var districtRepository: DistrictRepository

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
                    districtId = testDistrict.id!!,
                )

            // Act & Assert
            mockMvc
                .perform(
                    post("/api/v1/municipalities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(request.name))
                .andExpect(jsonPath("$.data.code").value(request.code))
                .andExpect(jsonPath("$.data.type").value(request.type.name))
                .andExpect(jsonPath("$.data.isActive").value(true))
        }

        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should return 400 when creating municipality with invalid data`() {
            // Arrange
            val request =
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtId = testDistrict.id!!,
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
                    put("/api/v1/municipalities/${municipality.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(updateRequest.name))
                .andExpect(jsonPath("$.data.population").value(updateRequest.population))
        }
    }

    @Nested
    @DisplayName("Search Municipality Tests")
    inner class SearchMunicipalityTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should search municipalities with filters`() {
            // Arrange
            createTestMunicipalities()

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/municipalities/search")
                        .param("districtId", testDistrict.id.toString())
                        .param("type", MunicipalityType.MUNICIPALITY.name)
                        .param("minPopulation", "10000"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.totalElements").isNumber)
        }
    }

    @Nested
    @DisplayName("Geographic Search Tests")
    inner class GeographicSearchTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should find nearby municipalities`() {
            // Arrange
            createTestMunicipalityWithLocation()

            // Act & Assert
            mockMvc
                .perform(
                    get("/api/v1/municipalities/nearby")
                        .param("latitude", "27.7172")
                        .param("longitude", "85.3240")
                        .param("radiusKm", "10.0"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    inner class StatisticsTests {
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should get municipality statistics`() {
            // Arrange
            val municipality = createTestMunicipality()

            // Act & Assert
            mockMvc
                .perform(get("/api/v1/municipalities/${municipality.id}/statistics"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.totalWards").exists())
                .andExpect(jsonPath("$.data.activeWards").exists())
                .andExpect(jsonPath("$.data.totalPopulation").exists())
        }
    }

    private fun createAndPersistDistrict(): District = districtRepository.save(DistrictTestFixtures.createDistrict())

    private fun createTestMunicipality() =
        municipalityService.createMunicipality(
            MunicipalityTestFixtures.createMunicipalityRequest(districtId = testDistrict.id!!),
        )

    private fun createTestMunicipalities() {
        repeat(5) { i ->
            municipalityService.createMunicipality(
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtId = testDistrict.id!!,
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
                districtId = testDistrict.id!!,
                code = "GEO-TEST",
                latitude = BigDecimal("27.7172"),
                longitude = BigDecimal("85.3240"),
            ),
        )
    }
}
