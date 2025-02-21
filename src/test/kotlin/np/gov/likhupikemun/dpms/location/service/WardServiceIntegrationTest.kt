package np.gov.likhupikemun.dpms.location.service

import np.gov.likhupikemun.dpms.location.api.dto.criteria.WardSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.mapper.WardMapper
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateWardRequest
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.location.repository.WardRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WardServiceIntegrationTest {
    @Autowired
    private lateinit var wardService: WardService

    @Autowired
    private lateinit var wardRepository: WardRepository

    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    @Autowired
    private lateinit var wardMapper: WardMapper

    private lateinit var testMunicipality: Municipality

    @BeforeEach
    fun setup() {
        testMunicipality = createAndPersistMunicipality()
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `should create ward successfully`() {
        // Arrange
        val request = createWardRequest()

        // Act
        val response = wardService.createWard(request)

        // Assert
        assertNotNull(response)
        assertEquals(request.wardNumber, response.wardNumber)
        assertEquals(request.area, response.area)
        assertEquals(request.population, response.population)
        assertEquals(request.latitude, response.latitude)
        assertEquals(request.longitude, response.longitude)
        assertEquals(request.officeLocation, response.officeLocation)
        assertEquals(request.officeLocationNepali, response.officeLocationNepali)
        assertEquals(testMunicipality.code, response.municipality.code)
    }

    @Test
    @WithMockUser(roles = ["SUPER_ADMIN"])
    fun `should throw exception when creating duplicate ward number`() {
        // Arrange
        val request = createWardRequest()

        // Act & Assert
        wardService.createWard(request)
        assertThrows<DuplicateWardNumberException> {
            wardService.createWard(request)
        }
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `should update ward successfully`() {
        // Arrange
        val ward = wardService.createWard(createWardRequest())
        val updateRequest = createUpdateRequest()

        // Act
        val response = wardService.updateWard(ward.wardNumber, testMunicipality.code, updateRequest)

        // Assert
        assertEquals(updateRequest.area, response.area)
        assertEquals(updateRequest.population, response.population)
        assertEquals(updateRequest.latitude, response.latitude)
        assertEquals(updateRequest.longitude, response.longitude)
        assertEquals(updateRequest.officeLocation, response.officeLocation)
        assertEquals(updateRequest.officeLocationNepali, response.officeLocationNepali)
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should search wards by criteria`() {
        // Arrange
        createTestWards()
        val criteria =
            WardSearchCriteria(
                municipalityCode = testMunicipality.code,
                minPopulation = 1000L,
                maxPopulation = 2000L,
                includeInactive = false,
            )

        // Act
        val result = wardService.searchWards(criteria)

        // Assert
        assertTrue(result.totalElements > 0)
        result.content.forEach { ward ->
            assertTrue(ward.population!! in 1000L..2000L)
            assertEquals(testMunicipality.code, ward.municipality.code)
        }
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `should find nearby wards`() {
        // Arrange
        createTestWards()
        val latitude = BigDecimal("27.7172")
        val longitude = BigDecimal("85.3240")
        val radiusKm = 5.0

        // Act
        val result = wardService.findNearbyWards(latitude, longitude, radiusKm, 0, 10)

        // Assert
        assertTrue(result.totalElements > 0)
    }

    private fun createWardRequest() =
        CreateWardRequest(
            municipalityCode = testMunicipality.code,
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

    private fun createTestWards() {
        for (i in 1..5) {
            wardService.createWard(
                CreateWardRequest(
                    municipalityCode = testMunicipality.code,
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
