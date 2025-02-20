package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.domain.Ward
import org.junit.jupiter.api.Assertions.*
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
import java.util.*

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
        testDistrict = createAndPersistDistrict()
        testMunicipality = createAndPersistMunicipality(testDistrict)
    }

    @Nested
    @DisplayName("Basic Query Tests")
    inner class BasicQueryTests {
        @Test
        fun `should find municipality by district ID`() {
            // Act
            val result = municipalityRepository.findByDistrictId(testDistrict.id!!)

            // Assert
            assertEquals(1, result.size)
            assertEquals(testMunicipality.id, result[0].id)
        }

        @Test
        fun `should find active municipalities by district ID`() {
            // Arrange
            val inactiveMunicipality = createMunicipality(testDistrict, "INACTIVE", isActive = false)
            entityManager.persist(inactiveMunicipality)
            entityManager.flush()

            // Act
            val result = municipalityRepository.findByDistrictIdAndIsActive(testDistrict.id!!, true)

            // Assert
            assertEquals(1, result.size)
            assertTrue(result.all { it.isActive })
        }

        @Test
        fun `should find municipality by code and district ID`() {
            // Act
            val result = municipalityRepository.findByCodeAndDistrictId("TEST001", testDistrict.id!!)

            // Assert
            assertTrue(result.isPresent)
            assertEquals(testMunicipality.id, result.get().id)
        }
    }

    @Nested
    @DisplayName("Geographic Query Tests")
    inner class GeographicQueryTests {
        @Test
        fun `should find nearby municipalities`() {
            // Arrange
            val nearbyMunicipality =
                createMunicipality(
                    testDistrict,
                    "NEARBY",
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                )
            entityManager.persist(nearbyMunicipality)
            entityManager.flush()

            // Act
            val result =
                municipalityRepository.findNearby(
                    BigDecimal("27.7172"),
                    BigDecimal("85.3240"),
                    5000.0, // 5km radius
                    PageRequest.of(0, 10),
                )

            // Assert
            assertTrue(result.totalElements > 0)
            assertTrue(result.content.any { it.id == nearbyMunicipality.id })
        }
    }

    @Nested
    @DisplayName("Type-based Query Tests")
    inner class TypeBasedQueryTests {
        @Test
        fun `should find municipalities by type`() {
            // Arrange
            val metropolitanCity =
                createMunicipality(
                    testDistrict,
                    "METRO",
                    type = MunicipalityType.METROPOLITAN_CITY,
                )
            entityManager.persist(metropolitanCity)
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
                municipalityRepository.findByTypeAndDistrictId(
                    MunicipalityType.MUNICIPALITY,
                    testDistrict.id!!,
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
                createMunicipality(
                    testDistrict,
                    "LARGE",
                    population = 100000,
                    area = BigDecimal("500.00"),
                )
            entityManager.persist(largeMunicipality)
            entityManager.flush()

            // Act
            val result =
                municipalityRepository.findLargeMunicipalities(
                    50000,
                    BigDecimal("300.00"),
                    PageRequest.of(0, 10),
                )

            // Assert
            assertTrue(result.totalElements > 0)
            assertTrue(
                result.content.all {
                    it.population!! >= 50000 && it.area!! >= BigDecimal("300.00")
                },
            )
        }
    }

    @Nested
    @DisplayName("Validation Query Tests")
    inner class ValidationQueryTests {
        @Test
        fun `should check existence by code and district`() {
            // Act & Assert
            assertTrue(
                municipalityRepository.existsByCodeAndDistrict(
                    "TEST001",
                    testDistrict.id!!,
                    null,
                ),
            )

            assertFalse(
                municipalityRepository.existsByCodeAndDistrict(
                    "TEST001",
                    testDistrict.id!!,
                    testMunicipality.id,
                ),
            )
        }
    }

    @Nested
    @DisplayName("Ward-related Query Tests")
    inner class WardRelatedQueryTests {
        @Test
        fun `should find municipalities with minimum wards`() {
            // Arrange
            addWardsToMunicipality(testMunicipality, 5)
            entityManager.flush()

            // Act
            val result = municipalityRepository.findByMinimumWards(3, PageRequest.of(0, 10))

            // Assert
            assertTrue(result.totalElements > 0)
            assertTrue(result.content.all { it.wards.size >= 3 })
        }
    }

    // Helper methods
    private fun createAndPersistDistrict(): District {
        val district =
            District().apply {
                name = "Test District"
                nameNepali = "परीक्षण जिल्ला"
                code = "TEST-D"
                isActive = true
            }
        return entityManager.persist(district)
    }

    private fun createAndPersistMunicipality(district: District): Municipality {
        val municipality = createMunicipality(district, "TEST001")
        return entityManager.persist(municipality)
    }

    private fun createMunicipality(
        district: District,
        code: String,
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        population: Long = 50000,
        area: BigDecimal = BigDecimal("100.00"),
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        isActive: Boolean = true,
    ): Municipality =
        Municipality().apply {
            this.name = "Municipality-$code"
            this.nameNepali = "नगरपालिका-$code"
            this.code = code
            this.type = type
            this.area = area
            this.population = population
            this.latitude = latitude
            this.longitude = longitude
            this.totalWards = 0
            this.isActive = isActive
            this.district = district
        }

    private fun addWardsToMunicipality(
        municipality: Municipality,
        count: Int,
    ) {
        repeat(count) { index ->
            val ward =
                Ward().apply {
                    wardNumber = index + 1
                    this.municipality = municipality
                    isActive = true
                }
            entityManager.persist(ward)
            municipality.wards.add(ward)
        }
    }
}
