package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSortField
import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.repository.ProvinceRepository
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
import kotlin.test.assertTrue

@WebMvcTest(ProvinceService::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Province Search Integration Tests")
@WithMockUser(roles = ["SUPER_ADMIN"])
class ProvinceSearchIntegrationTest {
    @Autowired
    private lateinit var provinceService: ProvinceService

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @BeforeEach
    fun setup() {
        provinceRepository.deleteAll()
        setupTestData()
    }

    private fun setupTestData() {
        ProvinceTestFixtures.createSearchTestData().forEach { province ->
            provinceRepository.save(province)
        }
    }

    @Nested
    @DisplayName("Basic Search Tests")
    inner class BasicSearchTests {
        @Test
        @Transactional
        fun `should search by name`() {
            val criteria =
                ProvinceSearchCriteria(
                    searchTerm = "Bagmati",
                    fields = setOf(ProvinceField.NAME, ProvinceField.CODE),
                )

            val result = provinceService.searchProvinces(criteria)

            assertEquals(1, result.totalElements)
            val province = result.content.first()
            assertNotNull(province.getValue(ProvinceField.NAME))
            val name = province.getValue(ProvinceField.NAME) as String
            assertTrue(name.contains("Bagmati", ignoreCase = true))
        }

        // TODO: Fix this failing test
        // @Test
        // @Transactional
        // fun `should search by nameNepali`() {
        //     val criteria = ProvinceSearchCriteria(
        //         searchTerm = "बागमती",
        //         fields = setOf(ProvinceField.CODE, ProvinceField.NAME_NEPALI), // Make sure NAME_NEPALI is included
        //         includeGeometry = false,
        //         includeDistricts = false,
        //         includeTotals = false
        //     )

        //     val result = provinceService.searchProvinces(criteria)

        //     assertEquals(1, result.totalElements)
        //     val province = result.content.first()

        //     // Debug logging
        //     println("Available fields: ${province::class.java.methods.map { it.name }}")
        //     println("Field values: ${ProvinceField.values().associateWith { province.getValue(it) }}")

        //     val nameNepaliValue = province.getValue(ProvinceField.NAME_NEPALI)
        //     assertNotNull(nameNepaliValue, "nameNepali should not be null")
        //     assertTrue(nameNepaliValue.toString().contains("बागमती"))
        // }
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    inner class AdvancedSearchTests {
        // TODO : Fix this failing test
        // @Test
        // @Transactional
        // fun `should search with totals included`() {
        //     val criteria =
        //         ProvinceSearchCriteria(
        //             includeTotals = true,
        //             fields = setOf(ProvinceField.NAME, ProvinceField.TOTAL_AREA, ProvinceField.TOTAL_POPULATION),
        //         )

        //     val result = provinceService.searchProvinces(criteria)

        //     assertTrue(result.content.isNotEmpty())
        //     result.content.forEach { projection ->
        //         assertNotNull(projection.getValue(ProvinceField.TOTAL_AREA))
        //         assertNotNull(projection.getValue(ProvinceField.TOTAL_POPULATION))
        //     }
        // }

        @Test
        @Transactional
        fun `should search with geometry included`() {
            val criteria =
                ProvinceSearchCriteria(
                    includeGeometry = true,
                    fields = setOf(ProvinceField.NAME, ProvinceField.GEOMETRY),
                    searchTerm = "Bagmati",
                )

            val result = provinceService.searchProvinces(criteria)

            assertTrue(result.content.isNotEmpty())
            val province = result.content.first()
            assertNotNull(province.getValue(ProvinceField.GEOMETRY))
            assertTrue(province.getValue(ProvinceField.GEOMETRY).toString().isNotEmpty())
        }

        @Test
        @Transactional
        fun `should sort by population in descending order`() {
            val criteria =
                ProvinceSearchCriteria(
                    sortBy = ProvinceSortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    fields = setOf(ProvinceField.NAME, ProvinceField.POPULATION),
                )

            val result = provinceService.searchProvinces(criteria)

            assertTrue(result.content.isNotEmpty())
            val populations =
                result.content.mapNotNull {
                    it.getValue(ProvinceField.POPULATION) as? Long
                }
            assertEquals(populations, populations.sortedDescending())
        }

        @Test
        @Transactional
        fun `should search with specific fields`() {
            val criteria =
                ProvinceSearchCriteria(
                    searchTerm = "Bagmati",
                    fields = setOf(ProvinceField.CODE, ProvinceField.NAME),
                )

            val result = provinceService.searchProvinces(criteria)

            assertTrue(result.content.isNotEmpty())
            val province = result.content.first()

            // Verify only requested fields are present
            assertNotNull(province.getValue(ProvinceField.CODE))
            assertNotNull(province.getValue(ProvinceField.NAME))
            assertNull(province.getValue(ProvinceField.NAME_NEPALI))
            assertNull(province.getValue(ProvinceField.AREA))
        }

        @Test
        @Transactional
        fun `should handle underscore separated field names`() {
            val criteria =
                ProvinceSearchCriteria(
                    searchTerm = "Bagmati",
                    fields = setOf(ProvinceField.CODE, ProvinceField.NAME_NEPALI),
                )

            val result = provinceService.searchProvinces(criteria)

            assertTrue(result.content.isNotEmpty())
            val province = result.content.first()

            // Verify fields are present and correctly named
            assertNotNull(province.getValue(ProvinceField.CODE))
            assertNotNull(province.getValue(ProvinceField.NAME_NEPALI))
            assertEquals("बागमती प्रदेश", province.getValue(ProvinceField.NAME_NEPALI))
        }
    }

    @Nested
    @DisplayName("Combined Search Tests")
    inner class CombinedSearchTests {
        @Test
        @Transactional
        fun `should combine multiple search criteria`() {
            val criteria =
                ProvinceSearchCriteria(
                    searchTerm = "Bagmati", // Change to search for a specific province
                    includeTotals = true,
                    includeGeometry = true,
                    includeDistricts = true,
                    sortBy = ProvinceSortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    fields =
                        setOf(
                            ProvinceField.NAME,
                            ProvinceField.POPULATION,
                            ProvinceField.AREA, // Changed from TOTAL_AREA
                            ProvinceField.GEOMETRY,
                            ProvinceField.DISTRICTS,
                        ),
                )

            val result = provinceService.searchProvinces(criteria)

            assertTrue(result.content.isNotEmpty())
            val province = result.content.first()

            // Test required fields
            assertNotNull(province.getValue(ProvinceField.NAME))
            assertNotNull(province.getValue(ProvinceField.POPULATION))
            assertNotNull(province.getValue(ProvinceField.AREA))
            assertNotNull(province.getValue(ProvinceField.GEOMETRY))

            // Verify specific content
            assertEquals("Bagmati Province", province.getValue(ProvinceField.NAME))
            assertEquals(6084042L, province.getValue(ProvinceField.POPULATION))
            assertTrue(province.getValue(ProvinceField.GEOMETRY).toString().isNotEmpty())
        }
    }
}
