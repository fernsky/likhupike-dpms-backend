package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Ward
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
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
class WardRepositoryIntegrationTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var wardRepository: WardRepository

    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    private lateinit var testMunicipality: Municipality
    private lateinit var testWard: Ward

    @BeforeEach
    fun setup() {
        testMunicipality = createAndPersistMunicipality()
        testWard = createAndPersistWard(testMunicipality)
    }

    @Test
    fun `should find ward by municipality ID`() {
        // Act
        val result = wardRepository.findByMunicipalityId(testMunicipality.id!!)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testWard.id, result[0].id)
    }

    @Test
    fun `should find active wards by municipality ID`() {
        // Arrange
        val inactiveWard = createWard(testMunicipality, 2, isActive = false)
        entityManager.persist(inactiveWard)
        entityManager.flush()

        // Act
        val result = wardRepository.findByMunicipalityIdAndIsActive(testMunicipality.id!!, true)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result.all { it.isActive })
    }

    @Test
    fun `should find ward by ward number and municipality ID`() {
        // Act
        val result = wardRepository.findByWardNumberAndMunicipalityId(1, testMunicipality.id!!)

        // Assert
        assertTrue(result.isPresent)
        assertEquals(testWard.id, result.get().id)
    }

    @Test
    fun `should find wards by population range`() {
        // Arrange
        val wardWithHighPopulation = createWard(testMunicipality, 2, population = 2000)
        entityManager.persist(wardWithHighPopulation)
        entityManager.flush()

        // Act
        val result =
            wardRepository.findByPopulationGreaterThanEqualAndPopulationLessThanEqual(
                1000,
                2000,
                PageRequest.of(0, 10),
            )

        // Assert
        assertEquals(2, result.totalElements)
        assertTrue(result.content.all { it.population!! in 1000..2000 })
    }

    @Test
    fun `should count active wards by municipality`() {
        // Arrange
        val inactiveWard = createWard(testMunicipality, 2, isActive = false)
        val activeWard = createWard(testMunicipality, 3, isActive = true)
        entityManager.persist(inactiveWard)
        entityManager.persist(activeWard)
        entityManager.flush()

        // Act
        val count = wardRepository.countByMunicipalityIdAndIsActive(testMunicipality.id!!, true)

        // Assert
        assertEquals(2, count) // testWard + activeWard
    }

    @Test
    fun `should check existence by ward number and municipality`() {
        // Act & Assert
        assertTrue(
            wardRepository.existsByWardNumberAndMunicipality(
                wardNumber = 1,
                municipalityId = testMunicipality.id!!,
                excludeId = null,
            ),
        )

        assertFalse(
            wardRepository.existsByWardNumberAndMunicipality(
                wardNumber = 1,
                municipalityId = testMunicipality.id!!,
                excludeId = testWard.id,
            ),
        )
    }

    private fun createAndPersistMunicipality(): Municipality {
        val municipality =
            Municipality().apply {
                name = "Test Municipality"
                nameNepali = "परीक्षण नगरपालिका"
                code = "TEST-01"
                type = np.gov.likhupikemun.dpms.location.domain.MunicipalityType.MUNICIPALITY
                area = BigDecimal("100.00")
                population = 10000L
                totalWards = 10
                isActive = true
            }
        return entityManager.persist(municipality)
    }

    private fun createAndPersistWard(municipality: Municipality): Ward {
        val ward = createWard(municipality, 1)
        return entityManager.persist(ward)
    }

    private fun createWard(
        municipality: Municipality,
        wardNumber: Int,
        population: Long = 1000L,
        isActive: Boolean = true,
    ): Ward =
        Ward().apply {
            this.municipality = municipality
            this.wardNumber = wardNumber
            this.area = BigDecimal("10.00")
            this.population = population
            this.latitude = BigDecimal("27.7172")
            this.longitude = BigDecimal("85.3240")
            this.officeLocation = "Ward $wardNumber Office"
            this.officeLocationNepali = "वडा $wardNumber कार्यालय"
            this.isActive = isActive
        }
}
