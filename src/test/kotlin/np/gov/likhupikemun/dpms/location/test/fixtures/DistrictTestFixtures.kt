package np.gov.likhupikemun.dpms.location.test.fixtures

import np.gov.likhupikemun.dpms.location.api.dto.request.CreateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Province
import java.math.BigDecimal

object DistrictTestFixtures {
    fun createDistrict(
        province: Province = ProvinceTestFixtures.createProvince(),
        name: String = "TEST DISTRICT",
        nameNepali: String = "परीक्षण जिल्ला".uppercase(),
        code: String = "TEST-D",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "TEST HEADQUARTER",
        headquarterNepali: String = "परीक्षण सदरमुकाम".uppercase(),
    ): District =
        District().apply {
            this.province = province
            this.name = name
            this.nameNepali = nameNepali
            this.code = code
            this.area = area
            this.population = population
            this.headquarter = headquarter
            this.headquarterNepali = headquarterNepali
        }

    fun createDistrictRequest(
        name: String = "TEST DISTRICT",
        nameNepali: String = "परीक्षण जिल्ला".uppercase(),
        code: String = "TEST-D",
        provinceCode: String = "TEST-P",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "TEST HEADQUARTER",
        headquarterNepali: String = "परीक्षण सदरमुकाम".uppercase(),
    ) = CreateDistrictRequest(
        name = name,
        nameNepali = nameNepali,
        code = code,
        provinceCode = provinceCode,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
    )

    fun createUpdateDistrictRequest(
        name: String = "UPDATED DISTRICT",
        nameNepali: String = "अद्यावधिक जिल्ला".uppercase(),
        area: BigDecimal = BigDecimal("1500.75"),
        population: Long = 150000L,
        headquarter: String = "UPDATED HEADQUARTER",
        headquarterNepali: String = "अद्यावधिक सदरमुकाम",
    ) = UpdateDistrictRequest(
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
    )

    fun createDistrictResponse(
        code: String = "TEST-D",
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        province: ProvinceSummaryResponse = ProvinceTestFixtures.createProvinceSummaryResponse(),
        municipalityCount: Int = 0,
        totalPopulation: Long? = 100000L,
        totalArea: BigDecimal? = BigDecimal("1000.50"),
    ) = DistrictResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        province = province,
        municipalityCount = municipalityCount,
        totalPopulation = totalPopulation,
        totalArea = totalArea,
    )

    fun createDistrictDetailResponse(
        code: String = "TEST-D",
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        province: ProvinceSummaryResponse = ProvinceTestFixtures.createProvinceSummaryResponse(),
        municipalities: List<MunicipalitySummaryResponse> = emptyList(),
    ) = DistrictDetailResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        province = province,
        municipalities = municipalities,
    )

    fun createDistrictSummaryResponse(
        code: String = "TEST-D",
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        municipalityCount: Int = 0,
    ) = DistrictSummaryResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        municipalityCount = municipalityCount,
    )

    fun createDistrictDetailResponse(
        code: String = "TEST-D1",
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        provinceCode: String,
    ) = DistrictDetailResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        province =
            ProvinceTestFixtures.createProvinceSummaryResponse(
                code = provinceCode,
                name = "Test Province",
            ),
        municipalities = emptyList(),
    )
}
