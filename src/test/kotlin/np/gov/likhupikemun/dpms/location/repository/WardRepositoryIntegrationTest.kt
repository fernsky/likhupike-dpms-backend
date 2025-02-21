package np.gov.likhupikemun.dpms.location.repository

import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.domain.Ward
import org.junit.jupiter.api.BeforeEach
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
        testMunicipality = createAndPersistTestMunicipality()
        testWard = createAndPersistWard(testMunicipality)
        entityManager.flush()
    }

    @Test
    fun `should find wards by municipality code`() {
        // Act
        val result = wardRepository.findByMunicipalityCode(testMunicipality.code!!)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testWard.wardNumber, result[0].wardNumber)
    }

    @Test
    fun `should find ward by ward number and municipality code`() {
        // Act
        val result =
            wardRepository.findByWardNumberAndMunicipalityCode(
                wardNumber = testWard.wardNumber!!,
                municipalityCode = testMunicipality.code!!,
            )

        // Assert
        assertTrue(result.isPresent)
        assertEquals(testWard.wardNumber, result.get().wardNumber)
    }

    @Test
    fun `should find wards by ward number range`() {
        // Arrange
        createMultipleWards()

        // Act
        val result =
            wardRepository.findByWardNumberRange(
                municipalityCode = testMunicipality.code!!,
                fromWard = 1,
                toWard = 3,
            )

        // Assert
        assertEquals(3, result.size)
        assertTrue(result.all { it.wardNumber!! in 1..3 })
    }

    @Test
    fun `should find wards by population range`() {
        // Arrange
        createMultipleWards()

        // Act
        val result =
            wardRepository.findByPopulationRange(
                minPopulation = 1000,
                maxPopulation = 2000,
                pageable = PageRequest.of(0, 10),
            )

        // Assert
        assertTrue(result.content.isNotEmpty())
        assertTrue(result.content.all { it.population!! in 1000..2000 })
    }

    @Test
    fun `should count wards by municipality code`() {
        // Arrange
        createMultipleWards()

        // Act
        val count = wardRepository.countByMunicipalityCode(testMunicipality.code!!)

        // Assert
        assertEquals(5, count)
    }

    @Test
    fun `should find wards by district code`() {
        // Act
        val municipalityCode = testMunicipality.code!!
        val districtCode = municipalityCode.substring(0, 2)
        val result = wardRepository.findByDistrictCode(districtCode)

        // Assert
        assertFalse(result.isEmpty())
        assertTrue(
            result.all { ward ->
                ward.municipality?.code?.startsWith(districtCode) ?: false
            },
        )
    }

    @Test
    fun `should find wards by province code`() {
        // Act
        val result = wardRepository.findByProvinceCode(testMunicipality.code!!.substring(0, 1))

        // Assert
        assertFalse(result.isEmpty())
    }

    private fun createAndPersistTestMunicipality(): Municipality {
        val municipality =
            Municipality().apply {
                name = "Test Municipality"
                nameNepali = "परीक्षण नगरपालिका"
                code = "TEST-MUN"
                type = MunicipalityType.MUNICIPALITY
                totalWards = 10
            }
        return entityManager.persist(municipality)
    }

    private fun createAndPersistWard(municipality: Municipality): Ward {
        val ward =
            Ward().apply {
                this.municipality = municipality
                this.wardNumber = 1
                this.area = BigDecimal("10.00")
                this.population = 1000L
                this.latitude = BigDecimal("27.7172")
                this.longitude = BigDecimal("85.3240")
                this.officeLocation = "Test Office"
                this.officeLocationNepali = "परीक्षण कार्यालय"
            }
        return entityManager.persist(ward)
    }

    private fun createMultipleWards() {
        (1..5).forEach { i ->
            entityManager.persist(
                Ward().apply {
                    municipality = testMunicipality
                    wardNumber = i
                    area = BigDecimal("10.00")
                    population = (1000L * i)
                    latitude = BigDecimal("27.7172")
                    longitude = BigDecimal("85.3240")
                    officeLocation = "Ward $i Office"
                    officeLocationNepali = "वडा $i कार्यालय"
                },
            )
        }
        entityManager.flush()
    }
}
