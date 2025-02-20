package np.gov.likhupikemun.dpms.location.test.fixtures

import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Ward
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

object WardTestFixtures {
    fun createWard(
        id: UUID = UUID.randomUUID(),
        municipality: Municipality,
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
        isActive: Boolean = true,
    ): Ward =
        Ward().apply {
            this.id = id
            this.municipality = municipality
            this.wardNumber = wardNumber
            this.area = area
            this.population = population
            this.latitude = latitude
            this.longitude = longitude
            this.officeLocation = officeLocation
            this.officeLocationNepali = officeLocationNepali
            this.isActive = isActive
            this.createdAt = LocalDateTime.now()
            this.updatedAt = LocalDateTime.now()
        }

    fun createWardRequest(
        municipalityId: UUID,
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
    ): CreateWardRequest =
        CreateWardRequest(
            municipalityId = municipalityId,
            wardNumber = wardNumber,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            officeLocation = officeLocation,
            officeLocationNepali = officeLocationNepali,
        )

    fun createUpdateWardRequest(
        area: BigDecimal = BigDecimal("15.00"),
        population: Long = 1500L,
        latitude: BigDecimal = BigDecimal("27.7173"),
        longitude: BigDecimal = BigDecimal("85.3241"),
        officeLocation: String = "Updated Office",
        officeLocationNepali: String = "अद्यावधिक कार्यालय",
    ): UpdateWardRequest =
        UpdateWardRequest(
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            officeLocation = officeLocation,
            officeLocationNepali = officeLocationNepali,
        )

    fun createWardResponse(
        id: UUID = UUID.randomUUID(),
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
        isActive: Boolean = true,
        municipality: MunicipalitySummaryResponse = MunicipalityTestFixtures.createMunicipalitySummaryResponse(),
    ): WardResponse =
        WardResponse(
            id = id,
            wardNumber = wardNumber,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            officeLocation = officeLocation,
            officeLocationNepali = officeLocationNepali,
            isActive = isActive,
            municipality = municipality,
        )

    fun createWardDetailResponse(
        id: UUID = UUID.randomUUID(),
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
        isActive: Boolean = true,
        municipality: MunicipalitySummaryResponse = MunicipalityTestFixtures.createMunicipalitySummaryResponse(),
        stats: WardStats = createWardStats(),
    ): WardDetailResponse =
        WardDetailResponse(
            id = id,
            wardNumber = wardNumber,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            officeLocation = officeLocation,
            officeLocationNepali = officeLocationNepali,
            isActive = isActive,
            municipality = municipality,
            stats = stats,
        )

    fun createWardStats(
        totalFamilies: Long = 250L,
        totalPopulation: Long = 1000L,
        totalArea: BigDecimal = BigDecimal("100.50"),
        populationDensity: BigDecimal = BigDecimal("9.95"),
        demographicBreakdown: Map<String, Long> =
            mapOf(
                "BRAHMIN" to 400L,
                "CHHETRI" to 300L,
                "JANAJATI" to 300L,
            ),
        economicStats: WardEconomicStats = createWardEconomicStats(),
        infrastructureStats: WardInfrastructureStats = createWardInfrastructureStats(),
    ): WardStats =
        WardStats(
            totalFamilies = totalFamilies,
            totalPopulation = totalPopulation,
            totalArea = totalArea,
            populationDensity = populationDensity,
            demographicBreakdown = demographicBreakdown,
            economicStats = economicStats,
            infrastructureStats = infrastructureStats,
        )

    private fun createWardEconomicStats(
        averageMonthlyIncome: BigDecimal = BigDecimal("25000.00"),
        employedPopulation: Long = 450L,
        employmentRate: BigDecimal = BigDecimal("45.0"),
        bankAccountHolders: Long = 200L,
        socialSecurityBeneficiaries: Long = 50L,
    ): WardEconomicStats =
        WardEconomicStats(
            averageMonthlyIncome = averageMonthlyIncome,
            employedPopulation = employedPopulation,
            employmentRate = employmentRate,
            bankAccountHolders = bankAccountHolders,
            socialSecurityBeneficiaries = socialSecurityBeneficiaries,
        )

    private fun createWardInfrastructureStats(
        householdsWithElectricity: Long = 240L,
        householdsWithWaterSupply: Long = 230L,
        householdsWithToilet: Long = 235L,
        householdsWithInternet: Long = 180L,
        agricultureLandArea: BigDecimal = BigDecimal("50.25"),
    ): WardInfrastructureStats =
        WardInfrastructureStats(
            householdsWithElectricity = householdsWithElectricity,
            householdsWithWaterSupply = householdsWithWaterSupply,
            householdsWithToilet = householdsWithToilet,
            householdsWithInternet = householdsWithInternet,
            agricultureLandArea = agricultureLandArea,
        )
}
