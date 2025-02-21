package np.gov.likhupikemun.dpms.location.test.fixtures

import np.gov.likhupikemun.dpms.location.api.dto.request.CreateProvinceRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateProvinceRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.Province
import java.math.BigDecimal
import java.time.LocalDateTime

object ProvinceTestFixtures {
    fun createProvince(
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
        code: String = "TEST-P",
        area: BigDecimal = BigDecimal("10000.50"),
        population: Long = 500000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        isActive: Boolean = true,
    ): Province =
        Province().apply {
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

    fun createProvinceRequest(
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
        code: String = "TEST-P",
        area: BigDecimal = BigDecimal("10000.50"),
        population: Long = 500000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
    ) = CreateProvinceRequest(
        name = name,
        nameNepali = nameNepali,
        code = code,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
    )

    fun createUpdateProvinceRequest(
        name: String = "Updated Province",
        nameNepali: String = "अद्यावधिक प्रदेश",
        area: BigDecimal = BigDecimal("12000.75"),
        population: Long = 600000L,
        headquarter: String = "Updated Headquarter",
        headquarterNepali: String = "अद्यावधिक सदरमुकाम",
    ) = UpdateProvinceRequest(
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
    )

    fun createProvinceResponse(
        code: String = "TEST-P",
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
        area: BigDecimal = BigDecimal("10000.50"),
        population: Long = 500000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        districtCount: Int = 5,
        totalPopulation: Long = 450000L,
        totalArea: BigDecimal = BigDecimal("9500.25"),
    ) = ProvinceResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        districtCount = districtCount,
        totalPopulation = totalPopulation,
        totalArea = totalArea,
    )

    fun createProvinceDetailResponse(
        code: String = "TEST-P",
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
        area: BigDecimal = BigDecimal("10000.50"),
        population: Long = 500000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        districts: List<DistrictSummaryResponse> = createDistrictSummaries(),
    ) = ProvinceDetailResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        districts = districts,
    )

    fun createProvinceSummaryResponse(
        code: String = "TEST-P",
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
    ) = ProvinceSummaryResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
    )

    private fun createDistrictSummaries(): List<DistrictSummaryResponse> =
        (1..3).map { index ->
            DistrictSummaryResponse(
                code = "TEST-D$index",
                name = "Test District $index",
                nameNepali = "परीक्षण जिल्ला $index",
                municipalityCount = 5,
            )
        }

    fun createProvinceStats(
        totalDistricts: Int = 8,
        totalMunicipalities: Int = 40,
        totalPopulation: Long = 450000L,
        totalArea: BigDecimal = BigDecimal("9500.25"),
        populationDensity: BigDecimal = BigDecimal("47.37"),
        municipalityTypes: Map<String, Int> = createMunicipalityTypeBreakdown(),
    ) = ProvinceStats(
        totalDistricts = totalDistricts,
        totalMunicipalities = totalMunicipalities,
        totalPopulation = totalPopulation,
        totalArea = totalArea,
        populationDensity = populationDensity,
        municipalityTypes = municipalityTypes,
    )

    private fun createMunicipalityTypeBreakdown() =
        mapOf(
            "METROPOLITAN_CITY" to 1,
            "SUB_METROPOLITAN_CITY" to 2,
            "MUNICIPALITY" to 25,
            "RURAL_MUNICIPALITY" to 12,
        )
}
