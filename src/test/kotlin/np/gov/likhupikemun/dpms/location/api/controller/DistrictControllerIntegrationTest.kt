package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.service.DistrictService
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
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
import java.math.BigDecimal

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
            val request = DistrictTestFixtures.createDistrictRequest(provinceId = testProvince.id!!)

            // Act & Assert
            mockMvc.perform(
                post("/api/v1/districts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf())
            )
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(request.name))
                .andExpect(jsonPath("$.data.code").value(request.code))
                .andExpect(jsonPath("$.data.isActive").value(true))
        }

        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should return 403 when unauthorized user tries to create district`() {
            // Arrange
            val request = DistrictTestFixtures.createDistrictRequest(provinceId = testProvince.id!!)

            // Act & Assert
            mockMvc.perform(
                post("/api/v1/districts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf())
            )
                .andExpect(status().isForbidden)
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
            mockMvc.perform(
                put("/api/v1/districts/${district.id}")
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
    @DisplayName("Search District Tests")
    inner class SearchDistrictTests {
        
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should search districts with filters`() {
            // Arrange
            createTestDistricts()

            // Act & Assert
            mockMvc.perform(
                get("/api/v1/districts/search")
                    .param("provinceId", testProvince.id.toString())
                    .param("minPopulation", "50000")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.totalElements").isNumber)
        }
    }

    @Nested
    @DisplayName("Geographic Search Tests")
    inner class GeographicSearchTests {
        
        @Test
        @WithMockUser(roles = ["VIEWER"])
        fun `should find nearby districts`() {
            // Arrange
            createTestDistrictWithLocation()

            // Act & Assert
            mockMvc.perform(
                get("/api/v1/districts/nearby")
                    .param("latitude", "27.7172")
                    .param("longitude", "85.3240")
                    .param("radiusKm", "50.0")
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
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
            createTestMunicipalities(district.id)

            // Act & Assert
            mockMvc.perform(get("/api/v1/districts/${district.id}/statistics"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.totalMunicipalities").exists())
                .andExpect(jsonPath("$.data.activeMunicipalities").exists())
                .andExpect(jsonPath("$.data.totalPopulation").exists())
        }
    }

    @Nested
    @DisplayName("Deactivation Tests")
    inner class DeactivationTests {
        
        @Test
        @WithMockUser(roles = ["SUPER_ADMIN"])
        fun `should deactivate district successfully`() {
            // Arrange
            val district = createTestDistrict()

            // Act & Assert
            mockMvc.perform(
                delete("/api/v1/districts/${district.id}")
                    .with(csrf())
            )
                .andExpect(status().isOk)

            mockMvc.perform(get("/api/v1/districts/${district.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.isActive").value(false))
        }
    }

    private fun createTestDistrict() = districtService.createDistrict(
        DistrictTestFixtures.createDistrictRequest(provinceId = testProvince.id!!)
    )

    private fun createTestDistricts() {
        repeat(5) { i ->
            districtService.createDistrict(
                DistrictTestFixtures.createDistrictRequest(
                    provinceId = testProvince.id!!,
                    code = "TEST-D$i",
                    population = 50000L + (i * 10000L)
                )
            )
        }
    }

    private fun createTestDistrictWithLocation() {
        districtService.createDistrict(
            DistrictTestFixtures.createDistrictRequest(
                provinceId = testProvince.id!!,
                code = "GEO-TEST",
                latitude = BigDecimal("27.7172"),
                longitude = BigDecimal("85.3240")
            )
        )
    }

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
}
