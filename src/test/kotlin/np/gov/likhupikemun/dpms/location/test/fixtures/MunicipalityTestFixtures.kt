package np.gov.likhupikemun.dpms.location.test.fixtures

import np.gov.likhupikemun.dpms.location.api.dto.request.CreateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import java.math.BigDecimal

object MunicipalityTestFixtures {
    fun createMunicipality(
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
    ): Municipality =
        Municipality().apply {
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
        }

    fun createMunicipalityRequest(
        districtCode: String = "TEST-D",
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
    ) = CreateMunicipalityRequest(
        districtCode = districtCode,
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
    ) = UpdateMunicipalityRequest(
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        totalWards = totalWards,
    )

    fun createMunicipalityResponse(
        code: String = "TEST-M",
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
        isActive: Boolean = true,
        district: DistrictSummaryResponse = DistrictTestFixtures.createDistrictSummaryResponse(),
    ) = MunicipalityResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        type = type,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        totalWards = totalWards,
        district = district,
    )

    fun createMunicipalityDetailResponse(
        code: String = "TEST-M",
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
        isActive: Boolean = true,
        district: DistrictSummaryResponse = DistrictTestFixtures.createDistrictSummaryResponse(),
    ) = MunicipalityDetailResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        type = type,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        totalWards = totalWards,
        isActive = isActive,
        district = district,
    )

    fun createMunicipalitySummaryResponse(
        code: String = "TEST-M",
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        totalWards: Int = 12,
        isActive: Boolean = true,
    ) = MunicipalitySummaryResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        type = type,
        totalWards = totalWards,
    )

    fun createMunicipalityStats(
        totalWards: Int = 12,
        activeWards: Int = 10,
        totalPopulation: Long = 50000L,
        totalArea: BigDecimal = BigDecimal("100.50"),
        totalFamilies: Long = 10000L,
    ): MunicipalityStats =
        MunicipalityStats(
            totalWards = totalWards,
            activeWards = activeWards,
            totalPopulation = totalPopulation,
            totalArea = totalArea,
            totalFamilies = totalFamilies,
        )
}
