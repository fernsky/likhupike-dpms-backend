package np.gov.likhupikemun.dpms.location.service

import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.exception.ProvinceCodeExistsException
import np.gov.likhupikemun.dpms.location.exception.ProvinceNotFoundException
import np.gov.likhupikemun.dpms.location.exception.ProvinceOperationException
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Province Service Integration Tests")
class ProvinceServiceIntegrationTest {

    @Autowired
    private lateinit var provinceService: ProvinceService

    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var municipalityService: MunicipalityService

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @BeforeEach
    fun setup() {
        provinceRepository.deleteAll()
    }

    @Nested
    @DisplayName("Create Province Tests")
    inner class CreateProvinceTests {
        
        @Test
        @Transactional
        fun `should create province successfully`() {
            // Given
            val request = ProvinceTestFixtures.createProvinceRequest()

            // When
            val result = provinceService.createProvince(request)

            // Then
            assertNotNull(result)
            assertEquals(request.name, result.name)
            assertEquals(request.nameNepali, result.nameNepali)
            assertEquals(request.code, result.code)
            assertTrue(result.isActive)

            // Verify persistence
            val savedProvince = provinceRepository.findById(result.id).orElseThrow()
            assertEquals(request.name, savedProvince.name)
        }

        @Test
        @Transactional
        fun `should throw exception for duplicate province code`() {
            // Given
            val request = ProvinceTestFixtures.createProvinceRequest()
            provinceService.createProvince(request)

            // When & Then
            assertThrows<ProvinceCodeExistsException> {
                provinceService.createProvince(request)
            }
        }
    }

    @Nested
    @DisplayName("Update Province Tests")
    inner class UpdateProvinceTests {
        
        @Test
        @Transactional
        fun `should update province successfully`() {
            // Given
            val province = createTestProvince()
            val updateRequest = ProvinceTestFixtures.createUpdateProvinceRequest()

            // When
            val result = provinceService.updateProvince(province.id!!, updateRequest)

            // Then
            assertEquals(updateRequest.name, result.name)
            assertEquals(updateRequest.population, result.population)
        }
    }

    @Nested
    @DisplayName("Search Province Tests")
    inner class SearchProvinceTests {
        
        @Test
        @Transactional
        fun `should search provinces with criteria`() {
            // Given
            createTestProvinces()
            val criteria = ProvinceSearchCriteria(
                searchTerm = "Test",
                minPopulation = 400000L,
                sortBy = ProvinceSortField.POPULATION,
                sortDirection = Sort.Direction.DESC
            )

            // When
            val result = provinceService.searchProvinces(criteria)

            // Then
            assertTrue(result.totalElements > 0)
            assertTrue(result.content.all { it.population!! >= 400000L })
        }
    }

    @Nested
    @DisplayName("Statistics Tests")
    inner class StatisticsTests {
        
        @Test
        @Transactional
        fun `should calculate province statistics correctly`() {
            // Given
            val province = createTestProvince()
            val district = createTestDistrict(province.id!!)
            createTestMunicipalities(district.id)

            // When
            val stats = provinceService.getProvinceStatistics(province.id!!)

            // Then
            assertEquals(1, stats.totalDistricts)
            assertEquals(3, stats.totalMunicipalities)
            assertTrue(stats.totalPopulation > 0)
            assertTrue(stats.totalArea > BigDecimal.ZERO)
        }
    }

    @Nested
    @DisplayName("Deactivation Tests")
    inner class DeactivationTests {
        
        @Test
        @Transactional
        fun `should not deactivate province with active districts`() {
            // Given
            val province = createTestProvince()
            createTestDistrict(province.id!!)

            // When & Then
            assertThrows<ProvinceOperationException> {
                provinceService.deactivateProvince(province.id!!)
            }
        }

        @Test
        @Transactional
        fun `should deactivate province without active districts`() {
            // Given
            val province = createTestProvince()

            // When
            provinceService.deactivateProvince(province.id!!)

            // Then
            val updatedProvince = provinceService.getProvince(province.id!!)
            assertFalse(updatedProvince.isActive)
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
                    code = "TEST-M$i",
                    population = 10000L + (i * 1000)
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
}
