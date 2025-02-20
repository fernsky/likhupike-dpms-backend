package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateWardRequest
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.location.service.WardService
import org.junit.jupiter.api.BeforeEach
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
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WardControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    @Autowired
    private lateinit var wardService: WardService

    private lateinit var testMunicipality: Municipality

    @BeforeEach
    fun setup() {
        testMunicipality = createAndPersistMunicipality()
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should create ward successfully`() {
        // Arrange
        val request = createWardRequest()

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/wards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.wardNumber").value(request.wardNumber))
            .andExpect(jsonPath("$.data.area").value(request.area.toString()))
            .andExpect(jsonPath("$.data.population").value(request.population))
            .andExpect(jsonPath("$.data.isActive").value(true))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should return 400 when creating ward with invalid data`() {
        // Arrange
        val request = createWardRequest().copy(wardNumber = 0)

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/wards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should update ward successfully`() {
        // Arrange
        val ward = wardService.createWard(createWardRequest())
        val updateRequest =
            UpdateWardRequest(
                area = BigDecimal("15.00"),
                population = 1500L,
                latitude = BigDecimal("27.7173"),
                longitude = BigDecimal("85.3241"),
                officeLocation = "Updated Office",
                officeLocationNepali = "अद्यावधिक कार्यालय",
            )

        // Act & Assert
        mockMvc
            .perform(
                put("/api/v1/wards/${ward.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
                    .with(csrf()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.area").value(updateRequest.area.toString()))
            .andExpect(jsonPath("$.data.population").value(updateRequest.population))
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should get ward details successfully`() {
        // Arrange
        val ward = wardService.createWard(createWardRequest())

        // Act & Assert
        mockMvc
            .perform(get("/api/v1/wards/${ward.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value(ward.id.toString()))
            .andExpect(jsonPath("$.data.wardNumber").value(ward.wardNumber))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should search wards with filters`() {
        // Arrange
        createTestWards()

        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/wards/search")
                    .param("municipalityId", testMunicipality.id.toString())
                    .param("minPopulation", "1000")
                    .param("maxPopulation", "2000"),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.content").isArray)
            .andExpect(jsonPath("$.data.totalElements").isNumber)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should deactivate ward successfully`() {
        // Arrange
        val ward = wardService.createWard(createWardRequest())

        // Act & Assert
        mockMvc
            .perform(
                delete("/api/v1/wards/${ward.id}/deactivate")
                    .with(csrf()),
            ).andExpect(status().isOk)

        mockMvc
            .perform(get("/api/v1/wards/${ward.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.isActive").value(false))
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `viewer should not be able to create ward`() {
        // Arrange
        val request = createWardRequest()

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/wards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()),
            ).andExpect(status().isForbidden)
    }

    private fun createAndPersistMunicipality(): Municipality {
        val municipality =
            Municipality().apply {
                name = "Test Municipality"
                nameNepali = "परीक्षण नगरपालिका"
                code = "TEST-01"
                type = MunicipalityType.MUNICIPALITY
                area = BigDecimal("100.00")
                population = 10000L
                totalWards = 10
                isActive = true
            }
        return municipalityRepository.save(municipality)
    }

    private fun createWardRequest() =
        CreateWardRequest(
            municipalityId = testMunicipality.id!!,
            wardNumber = 1,
            area = BigDecimal("10.00"),
            population = 1000L,
            latitude = BigDecimal("27.7172"),
            longitude = BigDecimal("85.3240"),
            officeLocation = "Test Office",
            officeLocationNepali = "परीक्षण कार्यालय",
        )

    private fun createTestWards() {
        for (i in 1..5) {
            wardService.createWard(
                CreateWardRequest(
                    municipalityId = testMunicipality.id!!,
                    wardNumber = i,
                    area = BigDecimal("10.00"),
                    population = (1000L * i),
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                    officeLocation = "Ward $i Office",
                    officeLocationNepali = "वडा $i कार्यालय",
                ),
            )
        }
    }
}
