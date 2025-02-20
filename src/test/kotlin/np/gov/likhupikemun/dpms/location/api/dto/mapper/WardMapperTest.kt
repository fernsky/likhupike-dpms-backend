package np.gov.likhupikemun.dpms.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import np.gov.likhupikemun.dpms.location.api.dto.mapper.impl.WardMapperImpl
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.WardStats
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Ward
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

class WardMapperTest {
    private lateinit var municipalityMapper: MunicipalityMapper
    private lateinit var wardMapper: WardMapper

    @BeforeEach
    fun setup() {
        municipalityMapper = mockk()
        wardMapper = WardMapperImpl(municipalityMapper)
    }

    @Test
    fun `toResponse should map all fields correctly`() {
        // Arrange
        val ward = createTestWard()
        val expectedMunicipalitySummary = createTestMunicipalitySummary()
        every { municipalityMapper.toSummaryResponse(any()) } returns expectedMunicipalitySummary

        // Act
        val response = wardMapper.toResponse(ward)

        // Assert
        assertAll(
            { assertEquals(ward.id, response.id) },
            { assertEquals(ward.wardNumber, response.wardNumber) },
            { assertEquals(ward.area, response.area) },
            { assertEquals(ward.population, response.population) },
            { assertEquals(ward.latitude, response.latitude) },
            { assertEquals(ward.longitude, response.longitude) },
            { assertEquals(ward.officeLocation, response.officeLocation) },
            { assertEquals(ward.officeLocationNepali, response.officeLocationNepali) },
            { assertEquals(ward.isActive, response.isActive) },
            { assertEquals(expectedMunicipalitySummary, response.municipality) },
        )
    }

    @Test
    fun `toDetailResponse should map all fields including stats`() {
        // Arrange
        val ward = createTestWard()
        val stats = createTestWardStats()
        val expectedMunicipalitySummary = createTestMunicipalitySummary()
        every { municipalityMapper.toSummaryResponse(any()) } returns expectedMunicipalitySummary

        // Act
        val response = wardMapper.toDetailResponse(ward, stats)

        // Assert
        assertAll(
            { assertEquals(ward.id, response.id) },
            { assertEquals(ward.wardNumber, response.wardNumber) },
            { assertEquals(ward.area, response.area) },
            { assertEquals(ward.population, response.population) },
            { assertEquals(ward.latitude, response.latitude) },
            { assertEquals(ward.longitude, response.longitude) },
            { assertEquals(ward.officeLocation, response.officeLocation) },
            { assertEquals(ward.officeLocationNepali, response.officeLocationNepali) },
            { assertEquals(ward.isActive, response.isActive) },
            { assertEquals(expectedMunicipalitySummary, response.municipality) },
            { assertEquals(stats, response.stats) },
        )
    }

    @Test
    fun `toSummaryResponse should map essential fields`() {
        // Arrange
        val ward = createTestWard()

        // Act
        val response = wardMapper.toSummaryResponse(ward)

        // Assert
        assertAll(
            { assertEquals(ward.id, response.id) },
            { assertEquals(ward.wardNumber, response.wardNumber) },
            { assertEquals(ward.population, response.population) },
            { assertEquals(ward.isActive, response.isActive) },
        )
    }

    @Test
    fun `toResponse should throw exception when ward ID is null`() {
        // Arrange
        val ward = createTestWard().apply { id = null }

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            wardMapper.toResponse(ward)
        }
    }

    @Test
    fun `toResponse should throw exception when ward number is null`() {
        // Arrange
        val ward = createTestWard().apply { wardNumber = null }

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            wardMapper.toResponse(ward)
        }
    }

    @Test
    fun `toResponse should throw exception when municipality is null`() {
        // Arrange
        val ward = createTestWard().apply { municipality = null }

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            wardMapper.toResponse(ward)
        }
    }

    private fun createTestWard() =
        Ward().apply {
            id = UUID.randomUUID()
            wardNumber = 1
            area = BigDecimal("100.50")
            population = 1000L
            latitude = BigDecimal("27.7172")
            longitude = BigDecimal("85.3240")
            officeLocation = "Central Office"
            officeLocationNepali = "केन्द्रीय कार्यालय"
            isActive = true
            municipality =
                Municipality().apply {
                    id = UUID.randomUUID()
                    name = "Test Municipality"
                }
        }

    private fun createTestMunicipalitySummary() =
        MunicipalitySummaryResponse(
            id = UUID.randomUUID(),
            name = "Test Municipality",
            nameNepali = "परीक्षण नगरपालिका",
            code = "TEST-01",
            type = np.gov.likhupikemun.dpms.location.domain.MunicipalityType.MUNICIPALITY,
            totalWards = 10,
        )

    private fun createTestWardStats() =
        WardStats(
            totalFamilies = 250L,
            totalPopulation = 1000L,
            totalArea = BigDecimal("100.50"),
            populationDensity = BigDecimal("9.95"),
            demographicBreakdown =
                mapOf(
                    "BRAHMIN" to 400L,
                    "CHHETRI" to 300L,
                    "JANAJATI" to 300L,
                ),
            economicStats =
                np.gov.likhupikemun.dpms.location.api.dto.response.WardEconomicStats(
                    averageMonthlyIncome = BigDecimal("25000.00"),
                    employedPopulation = 450L,
                    employmentRate = BigDecimal("45.0"),
                    bankAccountHolders = 200L,
                    socialSecurityBeneficiaries = 50L,
                ),
            infrastructureStats =
                np.gov.likhupikemun.dpms.location.api.dto.response.WardInfrastructureStats(
                    householdsWithElectricity = 240L,
                    householdsWithWaterSupply = 230L,
                    householdsWithToilet = 235L,
                    householdsWithInternet = 180L,
                    agricultureLandArea = BigDecimal("50.25"),
                ),
        )
}
