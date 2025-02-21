package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
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
import java.time.LocalDateTime
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
        entityManager.flush()
    }

    @Test
    fun `should find wards by municipality code`() {
        // Act
        val result = wardRepository.findByMunicipalityCode(testMunicipality.code)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testWard.wardNumber, result[0].wardNumber)
    }

    @Test
    fun `should find ward by ward number and municipality code`() {
        // Act
        val result = wardRepository.findByWardNumberAndMunicipalityCode(testWard.wardNumber, testMunicipality.code)

        // Assert
        assertTrue(result.isPresent)
        assertEquals(testWard.wardNumber, result.get().wardNumber)
    }

    @Test
    fun `should find wards by ward number range`() {
        // Arrange
        createMultipleWards()

        // Act
        val result = wardRepository.findByWardNumberRange(testMunicipality.code, 1, 3)

        // Assert
        assertEquals(3, result.size)
        assertTrue(result.all { it.wardNumber in 1..3 })
    }

    @Test
    fun `should find wards by population range`() {
        // Arrange
        createMultipleWards()

        // Act
        val result = wardRepository.findByPopulationRange(1000, 2000, PageRequest.of(0, 10))

        // Assert
        assertTrue(result.content.isNotEmpty())
        assertTrue(result.content.all { it.population!! in 1000..2000 })
    }

    @Test
    fun `should count wards by municipality code`() {
        // Arrange
        createMultipleWards()

        // Act
        val count = wardRepository.countByMunicipalityCode(testMunicipality.code)

        // Assert
        assertEquals(5, count)
    }

    @Test
    fun `should find wards by district code`() {
        // Act
        val result = wardRepository.findByDistrictCode(testMunicipality.code.substring(0, 2))

        // Assert
        assertFalse(result.isEmpty())
        assertTrue(result.all { it.municipality.code.startsWith(testMunicipality.code.substring(0, 2)) })
    }

    @Test
    fun `should find wards by province code`() {
        // Act
        val result = wardRepository.findByProvinceCode(testMunicipality.code.substring(0, 1))

        // Assert
    }

    private fun createAndPersistWard(municipality: Municipality): Ward {
        val ward = createWard(municipality, 1)
        return entityManager.persist(ward)
    }

    private fun createWard(
        municipality: Municipality,
        wardNumber: Int,
        population: Long = 1000L,
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
        }

    private fun createMultipleWards() {
        for (i in 1..5) {
            Ward()
                .apply {
                    municipality = testMunicipality
                    wardNumber = i
                    area = BigDecimal("10.00")
                    population = (1000L * i)
                    latitude = BigDecimal("27.7172")
                    longitude = BigDecimal("85.3240")
                    officeLocation = "Ward $i Office"
                    officeLocationNepali = "वडा $i कार्यालय"
                    isActive = true
                }.let { entityManager.persist(it) }
        }
        entityManager.flush()
    }
}
