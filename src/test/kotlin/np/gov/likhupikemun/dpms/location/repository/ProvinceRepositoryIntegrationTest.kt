package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
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
            assertEquals(testProvince.id, resultLower.get().id)
            assertEquals(testProvince.id, resultUpper.get().id)
        }

        @Test
        fun `should find active provinces`() {
            // Arrange
            val inactiveProvince = ProvinceTestFixtures.createProvince(code = "INACTIVE", isActive = false)
            entityManager.persist(inactiveProvince)
            entityManager.flush()

            // Act
            val activeProvinces = provinceRepository.findByIsActive(true)
            val inactiveProvinces = provinceRepository.findByIsActive(false)

            // Assert
            assertEquals(1, activeProvinces.size)
            assertEquals(1, inactiveProvinces.size)
            assertTrue(activeProvinces.all { it.isActive })
            assertFalse(inactiveProvinces.any { it.isActive })
        }
    }

    @Nested
    @DisplayName("Complex Query Tests")
    inner class ComplexQueryTests {
        
        @Test
        fun `should find provinces with active municipalities`() {
            // Arrange
            val district = createAndPersistDistrict(testProvince)
            createAndPersistMunicipality(district)

            // Act
            val result = provinceRepository.findActiveProvincesWithActiveMunicipalities()

            // Assert
            assertEquals(1, result.size)
            assertTrue(result.all { province ->
                province.districts.any { district ->
                    district.municipalities.any { it.isActive }
                }
            })
        }

        @Test
        fun `should find large provinces`() {
            // Arrange
            val smallProvince = ProvinceTestFixtures.createProvince(
                code = "SMALL",
                area = BigDecimal("500.00"),
                population = 50000L
            )
            entityManager.persist(smallProvince)
            entityManager.flush()

            // Act
            val result = provinceRepository.findLargeProvinces(
                minArea = BigDecimal("1000.00"),
                minPopulation = 100000L,
                pageable = PageRequest.of(0, 10)
            )

            // Assert
            assertEquals(1, result.totalElements)
            assertTrue(result.content.all { 
                it.area!! >= BigDecimal("1000.00") && it.population!! >= 100000L 
            })
        }
    }

    @Nested
    @DisplayName("Validation Query Tests")
    inner class ValidationQueryTests {
        
        @Test
        fun `should check code existence correctly`() {
            // Act & Assert
            assertTrue(provinceRepository.existsByCode(testProvince.code!!, null))
            assertFalse(provinceRepository.existsByCode(testProvince.code!!, testProvince.id))
            assertFalse(provinceRepository.existsByCode("NON-EXISTENT", null))
        }
    }

    @Nested
    @DisplayName("Hierarchy Tests")
    inner class HierarchyTests {
        
        @Test
        fun `should load province with districts and municipalities`() {
            // Arrange
            val district = createAndPersistDistrict(testProvince)
            createAndPersistMunicipality(district)
            entityManager.flush()
            entityManager.clear()

            // Act
            val result = provinceRepository.findById(testProvince.id!!).orElseThrow()

            // Assert
            assertEquals(1, result.districts.size)
            assertEquals(1, result.districts.first().municipalities.size)
        }
    }

    private fun createAndPersistDistrict(province: Province): District {
        val district = DistrictTestFixtures.createDistrict(province = province)
        return entityManager.persist(district)
    }

    private fun createAndPersistMunicipality(district: District): Municipality {
        val municipality = MunicipalityTestFixtures.createMunicipality(district = district)
        return entityManager.persist(municipality)
    }
}
