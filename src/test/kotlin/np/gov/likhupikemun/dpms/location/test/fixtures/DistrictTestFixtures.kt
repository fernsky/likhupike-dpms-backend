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
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        code: String = "TEST-D",
        provinceId: UUID = UUID.randomUUID(),
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
    ): CreateDistrictRequest =
        CreateDistrictRequest(
            name = name,
            nameNepali = nameNepali,
            code = code,
            provinceId = provinceId,
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
    ): UpdateDistrictRequest =
        UpdateDistrictRequest(
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
    ): DistrictResponse =
        DistrictResponse(
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
    ): DistrictDetailResponse =
        DistrictDetailResponse(
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
    ): DistrictSummaryResponse =
        DistrictSummaryResponse(
            code = code,
            name = name,
            nameNepali = nameNepali,
            municipalityCount = municipalityCount,
        )
}
