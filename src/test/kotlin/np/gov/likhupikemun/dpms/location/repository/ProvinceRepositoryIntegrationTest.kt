package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Province Repository Integration Tests")
class ProvinceRepositoryIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    private lateinit var testProvince: Province

    @BeforeEach
    fun setup() {
        testProvince = entityManager.persist(ProvinceTestFixtures.createProvince())
        entityManager.flush()
    }

    @Nested
    @DisplayName("Basic Query Tests")
    inner class BasicQueryTests {
        @Test
        fun `should find province by code case-insensitive`() {
            // Act
            val resultLower = provinceRepository.findByCodeIgnoreCase(testProvince.code!!.lowercase())
            val resultUpper = provinceRepository.findByCodeIgnoreCase(testProvince.code!!.uppercase())

            // Assert
            assertTrue(resultLower.isPresent)
            assertTrue(resultUpper.isPresent)
            assertEquals(testProvince.code, resultLower.get().code)
            assertEquals(testProvince.code, resultUpper.get().code)
        }

        @Test
        fun `should check if code exists`() {
            // Act & Assert
            assertTrue(provinceRepository.existsByCode(testProvince.code!!))
            assertFalse(provinceRepository.existsByCode("NONEXISTENT"))
        }
    }

    @Nested
    @DisplayName("Search Query Tests")
    inner class SearchQueryTests {
        @Test
        fun `should find large provinces`() {
            // Arrange
            val smallProvince =
                ProvinceTestFixtures.createProvince(
                    code = "SMALL",
                    area = BigDecimal("500.00"),
                    population = 50000L,
                )
            entityManager.persist(smallProvince)
            entityManager.flush()

            // Act
            val result =
                provinceRepository.findLargeProvinces(
                    minArea = BigDecimal("1000.00"),
                    minPopulation = 100000L,
                    pageable = PageRequest.of(0, 10),
                )

            // Assert
            assertEquals(1, result.totalElements)
            assertTrue(
                result.content.all {
                    it.area!! >= BigDecimal("1000.00") && it.population!! >= 100000L
                },
            )
        }
    }

    @Test
    fun `should handle empty result sets properly`() {
        // Act
        val result =
            provinceRepository.findLargeProvinces(
                minArea = BigDecimal("10000.00"),
                minPopulation = 1000000L,
                pageable = PageRequest.of(0, 10),
            )

        // Assert
        assertEquals(0, result.totalElements)
        assertTrue(result.content.isEmpty())
    }

    @Test
    fun `should handle pagination correctly`() {
        // Arrange
        repeat(5) { i ->
            entityManager.persist(
                ProvinceTestFixtures.createProvince(
                    code = "TEST-P$i",
                    area = BigDecimal("1000.00"),
                    population = 100000L,
                ),
            )
        }
        entityManager.flush()

        // Act
        val page1 =
            provinceRepository.findLargeProvinces(
                minArea = BigDecimal("1000.00"),
                minPopulation = 100000L,
                pageable = PageRequest.of(0, 3),
            )
        val page2 =
            provinceRepository.findLargeProvinces(
                minArea = BigDecimal("1000.00"),
                minPopulation = 100000L,
                pageable = PageRequest.of(1, 3),
            )

        // Assert
        assertEquals(6, page1.totalElements) // including test province
        assertEquals(3, page1.content.size)
        assertEquals(3, page2.content.size)
    }

    private fun createAndPersistDistrict(province: Province): District {
        val district = DistrictTestFixtures.createDistrict(province = province)
        return entityManager.persist(district)
    }
}
