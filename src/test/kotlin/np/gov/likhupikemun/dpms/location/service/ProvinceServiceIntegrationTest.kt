package np.gov.likhupikemun.dpms.location.service

import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSortField
import np.gov.likhupikemun.dpms.location.exception.ProvinceCodeExistsException
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
            assertEquals(0, result.districtCount)

            // Verify persistence
            val savedProvince = provinceRepository.findByCodeIgnoreCase(result.code).orElseThrow()
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
            val result = provinceService.updateProvince(province.code, updateRequest)

            // Then
            assertEquals(updateRequest.name, result.name)
            assertEquals(updateRequest.population, result.population)
            assertEquals(province.code, result.code)
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
            val criteria =
                ProvinceSearchCriteria(
                    searchTerm = "Test",
                    sortBy = ProvinceSortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    page = 0,
                    pageSize = 10,
                )

            // When
            val result = provinceService.searchProvinces(criteria)

            // Then
            assertTrue(result.totalElements > 0)
            assertTrue(result.content.all { it.population!! >= 400000L })
        }
    }

    private fun createTestProvince() =
        provinceService.createProvince(
            ProvinceTestFixtures.createProvinceRequest(),
        )

    private fun createTestDistrictWithMunicipalities(provinceCode: String) {
        // Create district
        val district =
            districtService.createDistrict(
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = provinceCode,
                    code = "TEST-D1",
                ),
            )

        // Create municipalities
        repeat(3) { i ->
            municipalityService.createMunicipality(
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = district.code,
                    code = "TEST-M$i",
                    population = 10000L + (i * 1000),
                    area = BigDecimal("100.00").add(BigDecimal(i.toString())),
                ),
            )
        }
    }

    private fun createTestProvinces() {
        repeat(5) { i ->
            provinceService.createProvince(
                ProvinceTestFixtures.createProvinceRequest(
                    code = "TEST-P$i",
                    name = "Test Province $i",
                    population = 400000L + (i * 50000L),
                ),
            )
        }
    }
}
