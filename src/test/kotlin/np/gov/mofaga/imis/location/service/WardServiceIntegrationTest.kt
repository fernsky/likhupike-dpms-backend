package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.dto.criteria.WardSearchCriteria
import np.gov.mofaga.imis.location.api.dto.enums.WardField
import np.gov.mofaga.imis.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.mofaga.imis.location.api.dto.response.WardResponse
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.exception.*
import np.gov.mofaga.imis.location.repository.DistrictRepository
import np.gov.mofaga.imis.location.repository.MunicipalityRepository
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.repository.WardRepository
import np.gov.mofaga.imis.location.test.fixtures.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows as jupiterAssertThrows

@WebMvcTest(WardService::class)
@AutoConfigureMockMvc
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Ward Service Integration Tests")
class WardServiceIntegrationTest {
    @Autowired private lateinit var wardService: WardService

    @Autowired private lateinit var wardRepository: WardRepository

    @Autowired private lateinit var municipalityRepository: MunicipalityRepository

    @Autowired private lateinit var districtRepository: DistrictRepository

    @Autowired private lateinit var provinceRepository: ProvinceRepository

    private lateinit var testProvince: Province
    private lateinit var testDistrict: District
    private lateinit var testMunicipality: Municipality

    @BeforeEach
    fun setup() {
        // Clean up repositories
        wardRepository.deleteAll()
        municipalityRepository.deleteAll()
        districtRepository.deleteAll()
        provinceRepository.deleteAll()

        // Create test data hierarchy
        testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())

        testDistrict =
            districtRepository.save(
                DistrictTestFixtures.createDistrict(province = testProvince),
            )

        testMunicipality =
            municipalityRepository.save(
                MunicipalityTestFixtures.createMunicipality(district = testDistrict),
            )
    }

    @Nested
    @DisplayName("Create Ward Tests")
    inner class CreateWardTests {
        @Test
        @Transactional
        @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
        @DisplayName("Should create ward successfully")
        fun shouldCreateWard() {
            // Given
            val request =
                WardTestFixtures.createWardRequest(
                    municipalityCode = testMunicipality.code!!,
                )

            // When
            val result = wardService.createWard(request)

            // Then
            assertNotNull(result)
            assertEquals(request.wardNumber, result.wardNumber)
            assertEquals(request.area, result.area)
            assertEquals(request.population, result.population)
            assertEquals(request.latitude, result.latitude)
            assertEquals(request.longitude, result.longitude)
            assertEquals(request.officeLocation, result.officeLocation)
            assertEquals(request.officeLocationNepali, result.officeLocationNepali)
            assertEquals(testMunicipality.code, result.municipality.code)

            // Verify persistence
            val savedWard =
                wardRepository
                    .findByWardNumberAndMunicipalityCode(result.wardNumber, testMunicipality.code!!)
                    .orElseThrow()
            assertEquals(request.wardNumber, savedWard.wardNumber)
            assertEquals(testMunicipality.code, savedWard.municipality?.code)
        }

        @Test
        @Transactional
        @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
        @DisplayName("Should throw exception for duplicate ward number")
        fun shouldThrowExceptionForDuplicateWardNumber() {
            // Given
            val request = WardTestFixtures.createWardRequest(municipalityCode = testMunicipality.code!!)
            wardService.createWard(request)

            // When & Then
            jupiterAssertThrows(DuplicateWardNumberException::class.java) {
                wardService.createWard(request)
            }
        }

        @Test
        @Transactional
        @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
        @DisplayName("Should throw exception for non-existent municipality")
        fun shouldThrowExceptionForNonExistentMunicipality() {
            // Given
            val request = WardTestFixtures.createWardRequest(municipalityCode = "NON-EXISTENT")

            // When & Then
            jupiterAssertThrows(MunicipalityNotFoundException::class.java) {
                wardService.createWard(request)
            }
        }
    }

    @Nested
    @DisplayName("Update Ward Tests")
    inner class UpdateWardTests {
        @Test
        @Transactional
        @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
        @DisplayName("Should update ward successfully")
        fun shouldUpdateWard() {
            // Given
            val createRequest = WardTestFixtures.createWardRequest(municipalityCode = testMunicipality.code!!)
            val ward = wardService.createWard(createRequest)
            val updateRequest = WardTestFixtures.createUpdateWardRequest()

            // When
            val result = wardService.updateWard(ward.wardNumber, testMunicipality.code!!, updateRequest)

            // Then
            assertNotNull(result)
            assertEquals(updateRequest.area, result.area)
            assertEquals(updateRequest.population, result.population)
            assertEquals(updateRequest.latitude, result.latitude)
            assertEquals(updateRequest.longitude, result.longitude)
            assertEquals(updateRequest.officeLocation, result.officeLocation)
            assertEquals(updateRequest.officeLocationNepali, result.officeLocationNepali)

            // Verify persistence
            val updatedWard =
                wardRepository
                    .findByWardNumberAndMunicipalityCode(ward.wardNumber, testMunicipality.code!!)
                    .orElseThrow()
            assertEquals(updateRequest.area, updatedWard.area)
        }

        @Test
        @Transactional
        @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
        @DisplayName("Should throw exception when updating non-existent ward")
        fun shouldThrowExceptionForNonExistentWard() {
            // Given
            val updateRequest = WardTestFixtures.createUpdateWardRequest()

            // When & Then
            jupiterAssertThrows(WardNotFoundException::class.java) {
                wardService.updateWard(999, testMunicipality.code!!, updateRequest)
            }
        }
    }

    @Nested
    @DisplayName("Search Ward Tests")
    inner class SearchWardTests {
        @Test
        @Transactional
        @DisplayName("Should search wards with criteria")
        fun shouldSearchWithCriteria() {
            // Given
            createTestWards()
            val criteria =
                WardSearchCriteria(
                    municipalityCode = testMunicipality.code!!,
                    minPopulation = 1000L,
                    maxPopulation = 2000L,
                    page = 0,
                    pageSize = 10,
                )

            // When
            val result = wardService.searchWards(criteria)

            // Then
            assertTrue(result.totalElements > 0)
            result.content.forEach { ward ->
                val population = (ward.getValue(WardField.POPULATION) as Number).toLong()
                val municipality = ward.getValue(WardField.MUNICIPALITY) as MunicipalitySummaryResponse

                assertTrue(population in 1000L..2000L)
                assertEquals(testMunicipality.code, municipality.code)
            }
        }
    }

    @Nested
    @DisplayName("Geographic Search Tests")
    inner class GeographicSearchTests {
        @Test
        @Transactional
        @DisplayName("Should find nearby wards")
        fun shouldFindNearbyWards() {
            // Given
            val centralPoint = Pair(BigDecimal("27.7172"), BigDecimal("85.3240"))
            createTestWards()

            // When
            val result =
                wardService.findNearbyWards(
                    latitude = centralPoint.first,
                    longitude = centralPoint.second,
                    radiusKm = 10.0,
                    page = 0,
                    size = 10,
                )

            // Then
            assertTrue(result.totalElements > 0)
            result.content.forEach { ward ->
                // Get the ward details to access geographic coordinates
                val wardDetails = wardService.getWard(ward.wardNumber, testMunicipality.code!!)
                val distance =
                    calculateDistance(
                        centralPoint.first.toDouble(),
                        centralPoint.second.toDouble(),
                        wardDetails.latitude!!.toDouble(),
                        wardDetails.longitude!!.toDouble(),
                    )
                assertTrue(distance <= 10.0)
            }
        }
    }

    @Nested
    @DisplayName("Ward Detail Tests")
    inner class WardDetailTests {
        @Test
        @Transactional
        @DisplayName("Should get ward detail")
        fun shouldGetWardDetail() {
            // Given
            // First create ward with MUNICIPALITY_ADMIN role
            val createRequest = WardTestFixtures.createWardRequest(municipalityCode = testMunicipality.code!!)
            lateinit var createdWard: WardResponse

            // Use MUNICIPALITY_ADMIN role to create the ward
            runAs("MUNICIPALITY_ADMIN") {
                createdWard = wardService.createWard(createRequest)
            }

            // Then switch to VIEWER role to test getWardDetail
            runAs("VIEWER") {
                // When
                val result = wardService.getWardDetail(createdWard.wardNumber, testMunicipality.code!!)

                // Then
                assertNotNull(result)
                assertEquals(createdWard.wardNumber, result.wardNumber)
                assertEquals(createdWard.municipality.code, result.municipality.code)
            }
        }

        @Test
        @Transactional
        @WithMockUser(roles = ["VIEWER"])
        @DisplayName("Should throw exception for non-existent ward detail")
        fun shouldThrowExceptionForNonExistentWardDetail() {
            jupiterAssertThrows(WardNotFoundException::class.java) {
                wardService.getWardDetail(999, testMunicipality.code!!)
            }
        }
    }

    // Add helper method for role switching
    private fun runAs(
        role: String,
        block: () -> Unit,
    ) {
        val authentication = TestingAuthenticationToken("test-user", "password", "ROLE_$role")
        SecurityContextHolder.getContext().authentication = authentication
        try {
            block()
        } finally {
            SecurityContextHolder.clearContext()
        }
    }

    private fun createTestWards() {
        // Create wards with MUNICIPALITY_ADMIN role
        runAs("MUNICIPALITY_ADMIN") {
            (1..5).forEach { i ->
                wardService.createWard(
                    WardTestFixtures.createWardRequest(
                        municipalityCode = testMunicipality.code!!,
                        wardNumber = i,
                        population = (1000L * i),
                    ),
                )
            }
        }
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val r = 6371.0 // Earth's radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
