package np.gov.likhupikemun.dpms.location.test.fixtures

import np.gov.likhupikemun.dpms.location.api.dto.request.CreateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Province
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

object DistrictTestFixtures {
    fun createDistrict(
        id: UUID = UUID.randomUUID(),
        province: Province = ProvinceTestFixtures.createProvince(),
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        code: String = "TEST-D",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        isActive: Boolean = true,
    ): District =
        District().apply {
            this.id = id
            this.province = province
            this.name = name
            this.nameNepali = nameNepali
            this.code = code
            this.area = area
            this.population = population
            this.headquarter = headquarter
            this.headquarterNepali = headquarterNepali
            this.isActive = isActive
            this.createdAt = LocalDateTime.now()
            this.createdBy = "test-user"
            this.updatedAt = LocalDateTime.now()
            this.updatedBy = "test-user"
        }

    fun createDistrictRequest(
        provinceId: UUID = UUID.randomUUID(),
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        code: String = "TEST-D",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
    ): CreateDistrictRequest =
        CreateDistrictRequest(
            provinceId = provinceId,
            name = name,
            nameNepali = nameNepali,
            code = code,
            area = area,
            population = population,
            headquarter = headquarter,
            headquarterNepali = headquarterNepali,
        )

    fun createUpdateDistrictRequest(
        name: String = "Updated District",
        nameNepali: String = "अद्यावधिक जिल्ला",
        area: BigDecimal = BigDecimal("1500.75"),
        population: Long = 150000L,
        headquarter: String = "Updated Headquarter",
        headquarterNepali: String = "अद्यावधिक सदरमुकाम",
        isActive: Boolean = true,
    ): UpdateDistrictRequest =
        UpdateDistrictRequest(
            name = name,
            nameNepali = nameNepali,
            area = area,
            population = population,
            headquarter = headquarter,
            headquarterNepali = headquarterNepali,
            isActive = isActive,
        )

    fun createDistrictResponse(
        id: UUID = UUID.randomUUID(),
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        code: String = "TEST-D",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        isActive: Boolean = true,
        province: ProvinceSummaryResponse = ProvinceTestFixtures.createProvinceSummaryResponse(),
    ): DistrictResponse =
        DistrictResponse(
            id = id,
            name = name,
            nameNepali = nameNepali,
            code = code,
            area = area,
            population = population,
            headquarter = headquarter,
            headquarterNepali = headquarterNepali,
            isActive = isActive,
            province = province,
            createdAt = LocalDateTime.now(),
            createdBy = "test-user",
            updatedAt = LocalDateTime.now(),
            updatedBy = "test-user",
        )

    fun createDistrictDetailResponse(
        id: UUID = UUID.randomUUID(),
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        code: String = "TEST-D",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        isActive: Boolean = true,
        province: ProvinceDetailResponse = ProvinceTestFixtures.createProvinceDetailResponse(),
        stats: DistrictStats = createDistrictStats(),
    ): DistrictDetailResponse =
        DistrictDetailResponse(
            id = id,
            name = name,
            nameNepali = nameNepali,
            code = code,
            area = area,
            population = population,
            headquarter = headquarter,
            headquarterNepali = headquarterNepali,
            isActive = isActive,
            province = province,
            stats = stats,
            createdAt = LocalDateTime.now(),
            createdBy = "test-user",
            updatedAt = LocalDateTime.now(),
            updatedBy = "test-user",
        )

    fun createDistrictSummaryResponse(
        id: UUID = UUID.randomUUID(),
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        code: String = "TEST-D",
        isActive: Boolean = true,
    ): DistrictSummaryResponse =
        DistrictSummaryResponse(
            id = id,
            name = name,
            nameNepali = nameNepali,
            code = code,
            isActive = isActive,
        )

    fun createDistrictStats(
        id: UUID = UUID.randomUUID(),
        totalMunicipalities: Int = 10,
        activeMunicipalities: Int = 8,
        totalPopulation: Long = 500000L,
        totalArea: BigDecimal = BigDecimal("5000.00"),
        demographicBreakdown: Map<String, Long> = createDemographicBreakdown(),
        infrastructureStats: Map<String, BigDecimal> = createInfrastructureStats(),
    ): DistrictStats =
        DistrictStats(
            id = id,
            totalMunicipalities = totalMunicipalities,
            activeMunicipalities = activeMunicipalities,
            totalPopulation = totalPopulation,
            totalArea = totalArea,
            demographicBreakdown = demographicBreakdown,
            infrastructureStats = infrastructureStats,
        )

    private fun createDemographicBreakdown(): Map<String, Long> =
        mapOf(
            "BRAHMIN" to 150000L,
            "CHHETRI" to 120000L,
            "JANAJATI" to 130000L,
            "DALIT" to 80000L,
            "OTHERS" to 20000L,
        )

    private fun createInfrastructureStats(): Map<String, BigDecimal> =
        mapOf(
            "ROADS_KM" to BigDecimal("1500.75"),
            "SCHOOLS" to BigDecimal("250.00"),
            "HOSPITALS" to BigDecimal("25.00"),
            "GOVERNMENT_OFFICES" to BigDecimal("50.00"),
            "BANKS" to BigDecimal("75.00"),
        )
}
