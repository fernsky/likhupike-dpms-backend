package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Municipality Repository Integration Tests")
class MunicipalityRepositoryIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    private lateinit var testDistrict: District
    private lateinit var testMunicipality: Municipality

    @BeforeEach
    fun setup() {
        testDistrict = entityManager.persist(DistrictTestFixtures.createDistrict())
        testMunicipality = entityManager.persist(MunicipalityTestFixtures.createMunicipality(district = testDistrict))
        entityManager.flush()
    }

    @Nested
    @DisplayName("Basic Query Tests")
    inner class BasicQueryTests {
        @Test
        fun `should find municipality by district code`() {
            // Act
            val result = municipalityRepository.findByDistrictCode(testDistrict.code!!)

            // Assert
            assertEquals(1, result.size)
            assertEquals(testMunicipality.code, result[0].code)
        }

        @Test
        fun `should find municipality by code`() {
            // Act
            val result = municipalityRepository.findByCodeIgnoreCase(testMunicipality.code!!)

            // Assert
            assertTrue(result.isPresent)
            assertEquals(testMunicipality.code, result.get().code)
        }

        @Test
        fun `should find municipality by code and district code`() {
            // Act
            val result =
                municipalityRepository.findByCodeAndDistrictCode(
                    testMunicipality.code!!,
                    testDistrict.code!!,
                )

            // Assert
            assertTrue(result.isPresent)
            assertEquals(testMunicipality.code, result.get().code)
        }
    }

    @Nested
    @DisplayName("Geographic Query Tests")
    inner class GeographicQueryTests {
        @Test
        fun `should find nearby municipalities`() {
            // Arrange
            val nearbyMunicipality =
                entityManager.persist(
                    MunicipalityTestFixtures.createMunicipality(
                        district = testDistrict,
                        code = "NEARBY",
                        latitude = BigDecimal("27.7172"),
                        longitude = BigDecimal("85.3240"),
                    ),
                )
            entityManager.flush()

            // Act
            val result =
                municipalityRepository.findNearby(
                    BigDecimal("27.7172"),
                    BigDecimal("85.3240"),
                    5000.0,
                    PageRequest.of(0, 10),
                )

            // Assert
            assertTrue(result.totalElements > 0)
            assertTrue(result.content.any { it.code == nearbyMunicipality.code })
        }
    }

    @Nested
    @DisplayName("Type-based Query Tests")
    inner class TypeBasedQueryTests {
        @Test
        fun `should find municipalities by type`() {
            // Arrange
            val metropolitanCity =
                entityManager.persist(
                    MunicipalityTestFixtures.createMunicipality(
                        district = testDistrict,
                        code = "METRO",
                        type = MunicipalityType.METROPOLITAN_CITY,
                    ),
                )
            entityManager.flush()

            // Act
            val result = municipalityRepository.findByType(MunicipalityType.METROPOLITAN_CITY)

            // Assert
            assertEquals(1, result.size)
            assertEquals(MunicipalityType.METROPOLITAN_CITY, result[0].type)
        }

        @Test
        fun `should find municipalities by type and district`() {
            // Act
            val result =
                municipalityRepository.findByTypeAndDistrict(
                    MunicipalityType.MUNICIPALITY,
                    testDistrict.code!!,
                )

            // Assert
            assertTrue(result.isNotEmpty())
            assertTrue(result.all { it.type == MunicipalityType.MUNICIPALITY })
        }
    }

    @Nested
    @DisplayName("Statistical Query Tests")
    inner class StatisticalQueryTests {
        @Test
        fun `should find large municipalities`() {
            // Arrange
            val largeMunicipality =
                entityManager.persist(
                    MunicipalityTestFixtures.createMunicipality(
                        district = testDistrict,
                        code = "LARGE",
                        population = 100000,
                        area = BigDecimal("500.00"),
                    ),
                )
            entityManager.flush()

            // Act
            val result =
                municipalityRepository.findLargeMunicipalities(
                    50000,
                    BigDecimal("300.00"),
                    PageRequest.of(0, 10, Sort.by("population").descending()),
                )

            // Assert
            assertTrue(result.totalElements > 0)
            assertTrue(
                result.content.all {
                    it.population!! >= 50000 && it.area!! >= BigDecimal("300.00")
                },
            )
        }

        @Test
        fun `should count municipalities by type and district`() {
            // Act
            val result = municipalityRepository.countByTypeAndDistrict(testDistrict.code!!)

            // Assert
            assertTrue(result.containsKey(MunicipalityType.MUNICIPALITY))
            assertEquals(1L, result[MunicipalityType.MUNICIPALITY])
        }

        @Test
        fun `should get total population by district`() {
            // Act
            val result = municipalityRepository.getTotalPopulationByDistrict(testDistrict.code!!)

            // Assert
            assertNotNull(result)
            assertEquals(testMunicipality.population, result)
        }
    }

    @Test
    fun `should check existence by code and district`() {
        // Act & Assert
        assertTrue(
            municipalityRepository.existsByCodeAndDistrict(
                testMunicipality.code!!,
                testDistrict.code!!,
            ),
        )

        assertFalse(
            municipalityRepository.existsByCodeAndDistrict(
                "NONEXISTENT",
                testDistrict.code!!,
            ),
        )
    }
}
