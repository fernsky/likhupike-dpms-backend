package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSortField
import np.gov.mofaga.imis.location.api.dto.enums.DistrictField
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.exception.*
import np.gov.mofaga.imis.location.repository.DistrictRepository
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@WebMvcTest(DistrictService::class)
@Import(TestSecurityConfig::class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("District Service Integration Tests")
@WithMockUser(roles = ["SUPER_ADMIN"])
class DistrictServiceIntegrationTest {
    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    private lateinit var testProvince: Province

    @BeforeEach
    fun setup() {
        districtRepository.deleteAll()
        provinceRepository.deleteAll()
        testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())
    }

    @Nested
    @DisplayName("Create District Tests")
    inner class CreateDistrictTests {
        @Test
        @Transactional
        @DisplayName("Should create district successfully")
        fun shouldCreateDistrict() {
            // Given
            val request =
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = testProvince.code!!,
                )

            // When
            val result = districtService.createDistrict(request)

            // Then
            assertNotNull(result)
            assertEquals(request.name, result.name)
            assertEquals(request.nameNepali, result.nameNepali)
            assertEquals(request.code, result.code)
            assertEquals(request.area, result.area)
            assertEquals(request.population, result.population)
            assertEquals(request.headquarter, result.headquarter)
            assertEquals(request.headquarterNepali, result.headquarterNepali)

            // Verify persistence
            val savedDistrict = districtRepository.findByCodeIgnoreCase(result.code).orElseThrow()
            assertEquals(request.name, savedDistrict.name)
            assertEquals(testProvince.code, savedDistrict.province?.code)
        }

        @Test
        @Transactional
        @DisplayName("Should throw exception for duplicate district code in same province")
        fun shouldThrowExceptionForDuplicateCode() {
            // Given
            val request =
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = testProvince.code!!,
                    code = "TEST-D1",
                )
            districtService.createDistrict(request)

            // When & Then
            assertThrows<DistrictCodeExistsException> {
                districtService.createDistrict(request)
            }
        }
    }

    @Nested
    @DisplayName("Update District Tests")
    inner class UpdateDistrictTests {
        @Test
        @Transactional
        @DisplayName("Should update district successfully")
        fun shouldUpdateDistrict() {
            // Given
            val district =
                districtRepository.save(
                    DistrictTestFixtures.createDistrict(province = testProvince),
                )
            val updateRequest = DistrictTestFixtures.createUpdateDistrictRequest()

            // When
            val result = districtService.updateDistrict(district.code!!, updateRequest)

            // Then
            assertNotNull(result)
            assertEquals(updateRequest.name, result.name)
            assertEquals(updateRequest.nameNepali, result.nameNepali)
            assertEquals(updateRequest.area, result.area)
            assertEquals(updateRequest.population, result.population)
            assertEquals(updateRequest.headquarter, result.headquarter)

            // Verify persistence
            val updatedDistrict = districtRepository.findByCodeIgnoreCase(district.code!!).orElseThrow()
            assertEquals(updateRequest.name, updatedDistrict.name)
        }
    }

    @Nested
    @DisplayName("Search District Tests")
    inner class SearchDistrictTests {
        @Test
        @Transactional
        @DisplayName("Should search districts with criteria")
        fun shouldSearchWithCriteria() {
            // Given
            val timestamp = System.currentTimeMillis().toString().takeLast(2)
            val districts =
                listOf(
                    DistrictTestFixtures.createDistrict(
                        province = testProvince,
                        name = "Test District 1",
                        code = "TEST-D1-$timestamp", // Ensure unique code
                        population = 100000,
                    ),
                    DistrictTestFixtures.createDistrict(
                        province = testProvince,
                        name = "Test District 2",
                        code = "TEST-D2-$timestamp", // Ensure unique code
                        population = 200000,
                    ),
                )
            districtRepository.saveAll(districts)

            val criteria =
                DistrictSearchCriteria(
                    searchTerm = "Test",
                    code = null,
                    sortBy = DistrictSortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    page = 0,
                    pageSize = 10,
                )

            // When
            val result = districtService.searchDistricts(criteria)

            // Then
            assertEquals(2, result.totalElements) // Changed to expect 2 results
            val firstResult = result.content.first()
            assertEquals(
                "Test District 2",
                firstResult.getValue(DistrictField.NAME) as String,
            )
            assertEquals(
                200000L,
                firstResult.getValue(DistrictField.POPULATION) as Long,
            )
        }
    }

    @Nested
    @DisplayName("Geographic Search Tests")
    inner class GeographicSearchTests {
        @Test
        @Transactional
        @DisplayName("Should find nearby districts")
        fun shouldFindNearbyDistricts() {
            // Given
            val centralPoint = Pair(BigDecimal("27.7172"), BigDecimal("85.3240"))
            val district =
                districtRepository.save(
                    DistrictTestFixtures.createDistrict(province = testProvince),
                )
            // Add a municipality near the central point
            district.addMunicipality(
                MunicipalityTestFixtures.createMunicipality(
                    district = district,
                    latitude = centralPoint.first,
                    longitude = centralPoint.second,
                ),
            )
            districtRepository.save(district)

            // When
            val result =
                districtService.findNearbyDistricts(
                    latitude = centralPoint.first,
                    longitude = centralPoint.second,
                    radiusKm = 10.0,
                    page = 0,
                    size = 10,
                )

            // Then
            assertEquals(1, result.totalElements)
            assertEquals(district.name, result.content.first().name)
        }
    }
}
