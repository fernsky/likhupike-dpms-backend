package np.gov.likhupikemun.dpms.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.service.WardService
import np.gov.likhupikemun.dpms.location.test.fixtures.WardTestFixtures
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
    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var wardService: WardService

    private lateinit var testMunicipality: Municipality

    @BeforeEach
    fun setup() {
        testMunicipality = createTestMunicipality()
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `should create ward successfully`() {
        // Arrange
        val request = WardTestFixtures.createWardRequest()
        val response = WardTestFixtures.createWardResponse()
        whenever(wardService.createWard(any())).thenReturn(response)

        // Act & Assert
        mockMvc
            .perform(
                post("/api/v1/wards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(csrf()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(response.wardNumber))
            .andExpect(jsonPath("$.data.municipality.code").value(response.municipality.code))
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should update ward successfully`() {
        // Arrange
        val updateRequest = WardTestFixtures.createUpdateWardRequest()
        val response = WardTestFixtures.createWardResponse()
        whenever(wardService.updateWard(1, "TEST-M", updateRequest)).thenReturn(response)

        // Act & Assert
        mockMvc
            .perform(
                put("/api/v1/wards/TEST-M/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
                    .with(csrf()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(response.wardNumber))
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should get ward detail successfully`() {
        // Arrange
        val response = WardTestFixtures.createWardDetailResponse()
        whenever(wardService.getWardDetail(1, "TEST-M")).thenReturn(response)

        // Act & Assert
        mockMvc
            .perform(get("/api/v1/wards/TEST-M/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.wardNumber").value(1))
    }

    // Helper methods
    private fun createTestMunicipality() =
        Municipality().apply {
            name = "Test Municipality"
            nameNepali = "परीक्षण नगरपालिका"
            code = "TEST-M"
            type = MunicipalityType.MUNICIPALITY
            area = BigDecimal("100.00")
            population = 10000L
            totalWards = 10
        }
}
