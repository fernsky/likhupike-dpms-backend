package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.WardResponse
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.service.WardService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class WardControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var wardService: WardService

    private lateinit var testMunicipality: Municipality
    private lateinit var testWardResponse: WardResponse

    @BeforeEach
    fun setup() {
        testMunicipality = createTestMunicipality()
        testWardResponse = createTestWardResponse()
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `should create ward successfully`() {
        // Arrange
        val request = createWardRequest()
        whenever(wardService.createWard(any())).thenReturn(testWardResponse)

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/wards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(testWardResponse.wardNumber))
            .andExpect(jsonPath("$.data.municipality.code").value(testWardResponse.municipality.code))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should update ward successfully`() {
        // Arrange
        val updateRequest = createUpdateRequest()
        whenever(wardService.updateWard(1, "TEST-01", updateRequest)).thenReturn(testWardResponse)

        // Act & Assert
        mockMvc
            .perform(
                put("/api/v1/wards/TEST-01/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
                    .with(csrf()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(testWardResponse.wardNumber))
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should get ward detail successfully`() {
        // Arrange
        whenever(wardService.getWardDetail(1, "TEST-01")).thenReturn(createTestWardDetailResponse())

        // Act & Assert
        mockMvc
            .perform(get("/api/v1/wards/TEST-01/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(1))
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should search wards successfully`() {
        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/wards/search")
                    .param("municipalityCode", "TEST-01")
                    .param("minPopulation", "1000")
                    .param("maxPopulation", "5000"),
            ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should get wards by municipality`() {
        // Act & Assert
        mockMvc
            .perform(get("/api/v1/wards/by-municipality/TEST-01"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should find nearby wards`() {
        // Act & Assert
        mockMvc
            .perform(
                get("/api/v1/wards/nearby")
                    .param("latitude", "27.7172")
                    .param("longitude", "85.3240")
                    .param("radiusKm", "5.0"),
            ).andExpect(status().isOk)
    }

    private fun createTestMunicipality() =
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

    private fun createWardRequest() =
        CreateWardRequest(
            municipalityCode = "TEST-01",
            wardNumber = 1,
            area = BigDecimal("10.00"),
            population = 1000L,
            latitude = BigDecimal("27.7172"),
            longitude = BigDecimal("85.3240"),
            officeLocation = "Test Office",
            officeLocationNepali = "परीक्षण कार्यालय",
        )

    private fun createUpdateRequest() =
        UpdateWardRequest(
            area = BigDecimal("15.00"),
            population = 1500L,
            latitude = BigDecimal("27.7173"),
            longitude = BigDecimal("85.3241"),
            officeLocation = "Updated Office",
            officeLocationNepali = "अद्यावधिक कार्यालय",
        )

    private fun createTestWardResponse() =
        WardResponse(
            wardNumber = 1,
            area = BigDecimal("10.00"),
            population = 1000L,
            latitude = BigDecimal("27.7172"),
            longitude = BigDecimal("85.3240"),
            officeLocation = "Test Office",
            officeLocationNepali = "परीक्षण कार्यालय",
            municipality =
                MunicipalitySummaryResponse(
                    code = "TEST-01",
                    name = "Test Municipality",
                    nameNepali = "परीक्षण नगरपालिका",
                ),
        )

    private fun createTestWardDetailResponse() = createTestWardResponse()
}
