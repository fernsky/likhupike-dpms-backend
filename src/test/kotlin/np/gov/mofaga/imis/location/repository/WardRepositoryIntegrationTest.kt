package np.gov.mofaga.imis.location.repository

import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Ward
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.WardTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Ward Repository Integration Tests")
class WardRepositoryIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var wardRepository: WardRepository

    private lateinit var testDistrict: District
    private lateinit var testMunicipality: Municipality
    private lateinit var testWard: Ward

    @BeforeEach
    fun setup() {
        // Create test data hierarchy
        val testProvince = entityManager.persist(ProvinceTestFixtures.createProvince())
        testDistrict = entityManager.persist(DistrictTestFixtures.createDistrict(province = testProvince))
        testMunicipality = entityManager.persist(MunicipalityTestFixtures.createMunicipality(district = testDistrict))
        testWard = entityManager.persist(WardTestFixtures.createWard(municipality = testMunicipality))
        entityManager.flush()
    }

    @Nested
    @DisplayName("Basic Query Tests")
    inner class BasicQueryTests {
        @Test
        fun `should find wards by municipality code`() {
            val result = wardRepository.findByMunicipalityCode(testMunicipality.code!!)

            assertEquals(1, result.size)
            assertEquals(testWard.wardNumber, result[0].wardNumber)
        }

        @Test
        fun `should find ward by ward number and municipality code`() {
            val result =
                wardRepository.findByWardNumberAndMunicipalityCode(
                    wardNumber = testWard.wardNumber!!,
                    municipalityCode = testMunicipality.code!!,
                )

            assertTrue(result.isPresent)
            assertEquals(testWard.wardNumber, result.get().wardNumber)
        }
    }

    @Nested
    @DisplayName("Range Query Tests")
    inner class RangeQueryTests {
        @BeforeEach
        fun setupMultipleWards() {
            // Clear all existing data
            entityManager.clear()
            entityManager.entityManager.createNativeQuery("DELETE FROM wards").executeUpdate()
            entityManager.entityManager.createNativeQuery("DELETE FROM municipalities").executeUpdate()
            entityManager.entityManager.createNativeQuery("DELETE FROM districts").executeUpdate()
            entityManager.entityManager.createNativeQuery("DELETE FROM provinces").executeUpdate()

            // Create fresh test data with unique codes
            val testProvince =
                entityManager.persist(
                    ProvinceTestFixtures.createProvince(
                        code = "TEST-P-${System.currentTimeMillis().toString().substring(0, 2)}", // Ensure unique code
                        name = "Test Province for Range Query",
                    ),
                )

            testDistrict =
                entityManager.persist(
                    DistrictTestFixtures.createDistrict(
                        province = testProvince,
                        code = "TEST-D-${System.currentTimeMillis().toString().substring(0, 2)}", // Ensure unique code
                        name = "Test District for Range Query",
                    ),
                )

            testMunicipality =
                entityManager.persist(
                    MunicipalityTestFixtures.createMunicipality(
                        district = testDistrict,
                        code = "TEST-M-${System.currentTimeMillis().toString().substring(0, 2)}", // Ensure unique code
                        name = "Test Municipality for Range Query",
                    ),
                )

            // Create wards with unique ward numbers
            (1..5).forEach { i ->
                entityManager.persist(
                    WardTestFixtures.createWard(
                        municipality = testMunicipality,
                        wardNumber = i,
                        population = (1000L * i),
                    ),
                )
            }
            entityManager.flush()
        }

        @Test
        fun `should find wards by ward number range`() {
            val result =
                wardRepository.findByWardNumberRange(
                    municipalityCode = testMunicipality.code!!,
                    fromWard = 1,
                    toWard = 3,
                )

            assertEquals(3, result.size)
            assertTrue(result.all { it.wardNumber!! in 1..3 })
        }

        @Test
        fun `should find wards by population range`() {
            val result =
                wardRepository.findByPopulationRange(
                    minPopulation = 1000,
                    maxPopulation = 2000,
                    pageable = PageRequest.of(0, 10),
                )

            assertTrue(result.content.isNotEmpty())
            assertTrue(result.content.all { it.population!! in 1000..2000 })
        }
    }

    @Nested
    @DisplayName("Administrative Query Tests")
    inner class AdministrativeQueryTests {
        @Test
        fun `should find wards by district code`() {
            val districtCode = testDistrict.code!!
            val result = wardRepository.findByDistrictCode(districtCode)

            assertFalse(result.isEmpty())
            assertTrue(result.all { it.municipality?.district?.code == districtCode })
        }

        @Test
        fun `should find wards by province code`() {
            val provinceCode = testDistrict.province?.code!!
            val result = wardRepository.findByProvinceCode(provinceCode)

            assertFalse(result.isEmpty())
            assertTrue(
                result.all {
                    it.municipality
                        ?.district
                        ?.province
                        ?.code == provinceCode
                },
            )
        }

        @Test
        fun `should count wards by municipality code`() {
            val count = wardRepository.countByMunicipalityCode(testMunicipality.code!!)
            assertEquals(1, count)
        }
    }
}
