package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
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
@DisplayName("District Repository Integration Tests")
class DistrictRepositoryIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    private lateinit var testProvince: Province
    private lateinit var testDistrict: District

    @BeforeEach
    fun setup() {
        testProvince = entityManager.persist(ProvinceTestFixtures.createProvince())
        testDistrict = entityManager.persist(DistrictTestFixtures.createDistrict(province = testProvince))
        entityManager.flush()
    }

    @Nested
    @DisplayName("Basic Query Tests")
    inner class BasicQueryTests {
        @Test
        fun `should find district by code`() {
            // Act
            val result = districtRepository.findByCodeIgnoreCase(testDistrict.code!!)

            // Assert
            assertTrue(result.isPresent)
            assertEquals(testDistrict.code, result.get().code)
        }

        @Test
        fun `should find districts by province code`() {
            // Act
            val districts = districtRepository.findByProvinceCode(testProvince.code!!)

            // Assert
            assertEquals(1, districts.size)
            assertEquals(testDistrict.code, districts[0].code)
        }

        @Test
        fun `should check if district exists by code and province`() {
            // Act & Assert
            assertTrue(
                districtRepository.existsByCodeAndProvince(
                    testDistrict.code!!,
                    testProvince.code!!,
                ),
            )

            assertFalse(
                districtRepository.existsByCodeAndProvince(
                    "NONEXISTENT",
                    testProvince.code!!,
                ),
            )
        }
    }

    @Nested
    @DisplayName("Search Query Tests")
    inner class SearchQueryTests {
        @Test
        fun `should find large districts`() {
            // Arrange
            val largeDistrict =
                DistrictTestFixtures.createDistrict(
                    province = testProvince,
                    code = "LARGE-D", // Changed code to avoid conflict
                    population = 200000,
                    area = BigDecimal("2000.00"),
                )
            entityManager.persist(largeDistrict)
            entityManager.flush()

            // Act
            val result =
                districtRepository.findLargeDistricts(
                    minPopulation = 150000,
                    minArea = BigDecimal("1500.00"),
                    pageable = PageRequest.of(0, 10),
                )

            // Assert
            assertEquals(1, result.totalElements)
            assertTrue(
                result.content.all {
                    it.population!! >= 150000 && it.area!! >= BigDecimal("1500.00")
                },
            )
        }
    }

    private fun addMunicipalityToDistrict(
        district: District,
        count: Int,
    ) {
        repeat(count) { index ->
            val municipality =
                Municipality().apply {
                    name = "Municipality ${index + 1}"
                    nameNepali = "नगरपालिका ${index + 1}"
                    code = "MUN-${index + 1}"
                    type = MunicipalityType.MUNICIPALITY
                    this.district = district
                }
            entityManager.persist(municipality)
            district.municipalities.add(municipality)
        }
    }

    private fun addMunicipalityWithLocation(
        district: District,
        latitude: BigDecimal,
        longitude: BigDecimal,
    ) {
        val municipality =
            Municipality().apply {
                name = "Test Municipality"
                nameNepali = "परीक्षण नगरपालिका"
                code = "TEST-MUN"
                type = MunicipalityType.MUNICIPALITY
                this.latitude = latitude
                this.longitude = longitude
                this.district = district
            }
        entityManager.persist(municipality)
        district.municipalities.add(municipality)
    }
}
