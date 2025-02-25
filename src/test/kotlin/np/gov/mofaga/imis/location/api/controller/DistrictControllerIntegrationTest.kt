package np.gov.mofaga.imis.location.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.mofaga.imis.auth.test.UserTestDataFactory
import np.gov.mofaga.imis.config.IntegrationTest
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.repository.DistrictRepository
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import np.gov.mofaga.imis.test.clearDatabase
import np.gov.mofaga.imis.test.loginAs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.util.UUID
import javax.sql.DataSource

@IntegrationTest
@DisplayName("District Controller Integration Tests")
class DistrictControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var testProvince: Province
    private lateinit var testDistrict: District

    private val superAdmin = UserTestDataFactory.createSuperAdmin()
    private val viewer = UserTestDataFactory.createViewer()

    @BeforeEach
    fun setup() {
        // Create test province using fixture
        testProvince =
            ProvinceTestFixtures.createProvince(
                code = "TEST-P-${UUID.randomUUID().toString().substring(0, 2)}",
            )
        testProvince = provinceRepository.save(testProvince)

        // Create test district using fixture
        testDistrict =
            DistrictTestFixtures.createDistrict(
                province = testProvince,
                code = "TEST-D-${UUID.randomUUID().toString().substring(0, 2)}",
            )
        testDistrict = districtRepository.save(testDistrict)
    }

    @AfterEach
    fun cleanup() {
        clearDatabase(dataSource)
    }

    @Nested
    @DisplayName("Create District Tests")
    inner class CreateDistrictTests {
        @Test
        fun `should create district when super admin`() {
            loginAs(superAdmin)

            val request =
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = testProvince.code!!,
                    name = "New District",
                    nameNepali = "नयाँ जिल्ला",
                    code = "TEST-D-NEW",
                    area = BigDecimal("1000.50"),
                    population = 100000L,
                    headquarter = "Test HQ",
                    headquarterNepali = "परीक्षण सदरमुकाम",
                )

            mockMvc
                .perform(
                    post("/api/v1/districts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(request.code))
                .andExpect(jsonPath("$.data.name").value(request.name))
                .andExpect(jsonPath("$.data.nameNepali").value(request.nameNepali))
        }

        @Test
        fun `should return 403 when viewer tries to create district`() {
            loginAs(viewer)

            val request =
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = testProvince.code!!,
                )

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
            loginAs(viewer)

            mockMvc
                .perform(get("/api/v1/districts/${testDistrict.code}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.code").value(testDistrict.code))
                .andExpect(jsonPath("$.data.name").value(testDistrict.name))
        }

        @Test
        fun `should return 404 for non-existent district`() {
            loginAs(viewer)

            mockMvc
                .perform(get("/api/v1/districts/NONEXISTENT"))
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    @DisplayName("Update District Tests")
    inner class UpdateDistrictTests {
        @Test
        fun `should update district when super admin`() {
            loginAs(superAdmin)

            val updateRequest =
                DistrictTestFixtures.createUpdateDistrictRequest(
                    name = "Updated District",
                )

            mockMvc
                .perform(
                    put("/api/v1/districts/${testDistrict.code}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.name").value(updateRequest.name))
        }
    }

    @Nested
    @DisplayName("Search District Tests")
    inner class SearchDistrictTests {
        @Test
        fun `should search districts with criteria`() {
            loginAs(viewer)

            mockMvc
                .perform(
                    get("/api/v1/districts/search")
                        .param("provinceCode", testProvince.code)
                        .param("searchTerm", testDistrict.name)
                        .param("page", "0")
                        .param("size", "10"),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.data.content").isArray)
                .andExpect(jsonPath("$.data.content[0].code").value(testDistrict.code))
        }
    }

    @Nested
    @DisplayName("Province-based District Tests")
    inner class ProvinceBasedDistrictTests {
        @Test
        fun `should get districts by province`() {
            loginAs(viewer)

            mockMvc
                .perform(get("/api/v1/districts/by-province/${testProvince.code}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data").isArray)
                .andExpect(jsonPath("$.data[0].code").value(testDistrict.code))
        }
    }
}
