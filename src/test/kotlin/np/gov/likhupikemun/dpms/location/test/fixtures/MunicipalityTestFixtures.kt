package np.gov.likhupikemun.dpms.location.test.fixtures

import np.gov.likhupikemun.dpms.location.api.dto.request.CreateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

object MunicipalityTestFixtures {
    fun createMunicipality(
        id: UUID = UUID.randomUUID(),
        district: District = DistrictTestFixtures.createDistrict(),
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
        isActive: Boolean = true,
    ): Municipality =
        Municipality().apply {
            this.id = id
            this.district = district
            this.name = name
            this.nameNepali = nameNepali
            this.code = code
            this.type = type
            this.area = area
            this.population = population
            this.latitude = latitude
            this.longitude = longitude
            this.totalWards = totalWards
            this.isActive = isActive
            this.createdAt = LocalDateTime.now()
            this.createdBy = "test-user"
            this.updatedAt = LocalDateTime.now()
            this.updatedBy = "test-user"
        }

    fun createMunicipalityRequest(
        districtId: UUID = UUID.randomUUID(),
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
    ): CreateMunicipalityRequest =
        CreateMunicipalityRequest(
            districtId = districtId,
            name = name,
            nameNepali = nameNepali,
            code = code,
            type = type,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            totalWards = totalWards,
        )

    fun createUpdateMunicipalityRequest(
        name: String = "Updated Municipality",
        nameNepali: String = "अद्यावधिक नगरपालिका",
        area: BigDecimal = BigDecimal("150.75"),
        population: Long = 75000L,
        latitude: BigDecimal = BigDecimal("27.7173"),
        longitude: BigDecimal = BigDecimal("85.3241"),
        totalWards: Int = 15,
        isActive: Boolean = true,
    ): UpdateMunicipalityRequest =
        UpdateMunicipalityRequest(
            name = name,
            nameNepali = nameNepali,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            totalWards = totalWards,
            isActive = isActive,
        )

    fun createMunicipalityResponse(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
        isActive: Boolean = true,
        district: DistrictSummaryResponse = DistrictTestFixtures.createDistrictSummaryResponse(),
    ): MunicipalityResponse =
        MunicipalityResponse(
            id = id,
            name = name,
            nameNepali = nameNepali,
            code = code,
            type = type,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            totalWards = totalWards,
            isActive = isActive,
            district = district,
            createdAt = LocalDateTime.now(),
            createdBy = "test-user",
            updatedAt = LocalDateTime.now(),
            updatedBy = "test-user",
        )

    fun createMunicipalityDetailResponse(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
        isActive: Boolean = true,
        district: DistrictSummaryResponse = DistrictTestFixtures.createDistrictSummaryResponse(),
        stats: MunicipalityStats = createMunicipalityStats(),
    ): MunicipalityDetailResponse =
        MunicipalityDetailResponse(
            id = id,
            name = name,
            nameNepali = nameNepali,
            code = code,
            type = type,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            totalWards = totalWards,
            isActive = isActive,
            district = district,
            stats = stats,
            createdAt = LocalDateTime.now(),
            createdBy = "test-user",
            updatedAt = LocalDateTime.now(),
            updatedBy = "test-user",
        )

    fun createMunicipalitySummaryResponse(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        totalWards: Int = 12,
        isActive: Boolean = true,
    ): MunicipalitySummaryResponse =
        MunicipalitySummaryResponse(
            id = id,
            name = name,
            nameNepali = nameNepali,
            code = code,
            type = type,
            totalWards = totalWards,
            isActive = isActive,
        )

    fun createMunicipalityStats(
        totalWards: Int = 12,
        activeWards: Int = 10,
        totalPopulation: Long = 50000L,
        totalArea: BigDecimal = BigDecimal("100.50"),
        totalFamilies: Long = 10000L,
        wardStats: List<WardStatistics> = createWardStatistics(),
        demographicStats: DemographicStats = createDemographicStats(),
        economicStats: EconomicStats = createEconomicStats(),
        infrastructureStats: InfrastructureStats = createInfrastructureStats(),
    ): MunicipalityStats =
        MunicipalityStats(
            totalWards = totalWards,
            activeWards = activeWards,
            totalPopulation = totalPopulation,
            totalArea = totalArea,
            totalFamilies = totalFamilies,
            wardStats = wardStats,
            demographicStats = demographicStats,
            economicStats = economicStats,
            infrastructureStats = infrastructureStats,
        )

    private fun createWardStatistics(): List<WardStatistics> =
        listOf(
            WardStatistics(
                wardNumber = 1,
                population = 5000L,
                area = BigDecimal("10.50"),
                familyCount = 1000L,
                isActive = true,
            ),
            WardStatistics(
                wardNumber = 2,
                population = 4500L,
                area = BigDecimal("9.75"),
                familyCount = 900L,
                isActive = true,
            ),
        )

    private fun createDemographicStats(): DemographicStats =
        DemographicStats(
            ethnicityBreakdown =
                mapOf(
                    "BRAHMIN" to 15000L,
                    "CHHETRI" to 12000L,
                    "JANAJATI" to 13000L,
                    "OTHERS" to 10000L,
                ),
            ageGroupBreakdown =
                mapOf(
                    "0-14" to 12000L,
                    "15-64" to 35000L,
                    "65+" to 3000L,
                ),
            genderBreakdown =
                mapOf(
                    "MALE" to 24000L,
                    "FEMALE" to 25000L,
                    "OTHER" to 1000L,
                ),
        )

    private fun createEconomicStats(): EconomicStats =
        EconomicStats(
            averageIncome = BigDecimal("35000.00"),
            employmentRate = BigDecimal("65.5"),
            povertyRate = BigDecimal("15.5"),
            businessCount = 500L,
        )

    private fun createInfrastructureStats(): InfrastructureStats =
        InfrastructureStats(
            roadCoverageKm = BigDecimal("150.75"),
            schoolCount = 25L,
            healthFacilityCount = 10L,
            waterAccessPercent = BigDecimal("85.5"),
            electricityAccessPercent = BigDecimal("95.0"),
        )
}
