package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSortField
import np.gov.mofaga.imis.location.api.dto.enums.DistrictField
import np.gov.mofaga.imis.location.api.dto.response.ProvinceSummaryResponse
import np.gov.mofaga.imis.location.repository.DistrictRepository
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@WebMvcTest(DistrictService::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("District Search Integration Tests")
@WithMockUser(roles = ["SUPER_ADMIN"])
class DistrictSearchIntegrationTest {
    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @BeforeEach
    fun setup() {
        // Clean up both repositories
        districtRepository.deleteAll()
        provinceRepository.deleteAll()
        setupTestData()
    }

    private fun setupTestData() {
        // First create and save a test province
        val testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())

        // Create districts with the saved province
        DistrictTestFixtures.createSearchTestData().map { district ->
            district.province = testProvince
            districtRepository.save(district)
        }
    }

    @Nested
    @DisplayName("Basic Search Tests")
    inner class BasicSearchTests {
        @Test
        @Transactional
        fun `should search by name with specific fields`() {
            val criteria =
                DistrictSearchCriteria(
                    searchTerm = "Kathmandu",
                    fields = setOf(DistrictField.NAME, DistrictField.CODE),
                )

            val result = districtService.searchDistricts(criteria)
            val district = result.content.first()

            assertNotNull(district.getValue(DistrictField.NAME))
            assertNotNull(district.getValue(DistrictField.CODE))
            assertNull(district.getValue(DistrictField.AREA))
        }
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    inner class AdvancedSearchTests {
        @Test
        @Transactional
        fun `should search with geometry included`() {
            val criteria =
                DistrictSearchCriteria(
                    fields = setOf(DistrictField.NAME, DistrictField.GEOMETRY),
                    includeGeometry = true,
                )

            val result = districtService.searchDistricts(criteria)
            val district = result.content.first()

            assertNotNull(district.getValue(DistrictField.GEOMETRY))
        }

        @Test
        @Transactional
        fun `should sort by population in descending order`() {
            val criteria =
                DistrictSearchCriteria(
                    sortBy = DistrictSortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    fields = setOf(DistrictField.NAME, DistrictField.POPULATION),
                )

            val result = districtService.searchDistricts(criteria)
            val populations =
                result.content.mapNotNull {
                    it.getValue(DistrictField.POPULATION) as? Long
                }
            assertEquals(populations, populations.sortedDescending())
        }
    }

    @Nested
    @DisplayName("Filter Tests")
    inner class FilterTests {
        @Test
        @Transactional
        fun `should filter districts by province code`() {
            // Given
            val province1 = provinceRepository.save(ProvinceTestFixtures.createProvince(code = "P1"))
            val province2 = provinceRepository.save(ProvinceTestFixtures.createProvince(code = "P2"))

            // Create districts in different provinces
            districtRepository.save(DistrictTestFixtures.createDistrict(code = "D1", province = province1))
            districtRepository.save(DistrictTestFixtures.createDistrict(code = "D2", province = province1))
            districtRepository.save(DistrictTestFixtures.createDistrict(code = "D3", province = province2))

            val criteria =
                DistrictSearchCriteria(
                    provinceCode = "P1",
                    fields = setOf(DistrictField.CODE, DistrictField.PROVINCE),
                )

            // When
            val result = districtService.searchDistricts(criteria)

            // Then
            assertEquals(2, result.totalElements)
            result.content.forEach { district ->
                val provinceData = district.getValue(DistrictField.PROVINCE)
                assertNotNull(provinceData)
                assertEquals("P1", (provinceData as ProvinceSummaryResponse).code)
            }
        }
    }
}
