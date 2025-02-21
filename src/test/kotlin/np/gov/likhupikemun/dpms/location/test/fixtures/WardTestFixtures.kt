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
        municipalityCode: String = "test-municipality",
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
    ): CreateWardRequest =
        CreateWardRequest(
            municipalityCode = municipalityCode,
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
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
        municipality: MunicipalitySummaryResponse = MunicipalityTestFixtures.createMunicipalitySummaryResponse(),
    ): WardResponse =
        WardResponse(
            wardNumber = wardNumber,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            officeLocation = officeLocation,
            officeLocationNepali = officeLocationNepali,
            municipality = municipality,
        )

    fun createWardDetailResponse(
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
        municipality: MunicipalitySummaryResponse = MunicipalityTestFixtures.createMunicipalitySummaryResponse(),
        stats: WardStats = createWardStats(),
    ): WardDetailResponse =
        WardDetailResponse(
            wardNumber = wardNumber,
            area = area,
            population = population,
            latitude = latitude,
            longitude = longitude,
            officeLocation = officeLocation,
            officeLocationNepali = officeLocationNepali,
            municipality = municipality,
        )
}
