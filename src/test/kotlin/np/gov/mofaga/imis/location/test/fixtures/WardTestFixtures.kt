package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.request.CreateWardRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateWardRequest
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Ward
import java.math.BigDecimal

object WardTestFixtures {
    fun createWard(
        municipality: Municipality = MunicipalityTestFixtures.createMunicipality(),
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
    ) = Ward().apply {
        this.municipality = municipality
        this.wardNumber = wardNumber
        this.area = area
        this.population = population
        this.latitude = latitude
        this.longitude = longitude
        this.officeLocation = officeLocation
        this.officeLocationNepali = officeLocationNepali
    }

    fun createWardRequest(
        municipalityCode: String = "TEST-M",
        wardNumber: Int = 1,
        area: BigDecimal = BigDecimal("10.00"),
        population: Long = 1000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        officeLocation: String = "Test Office",
        officeLocationNepali: String = "परीक्षण कार्यालय",
    ) = CreateWardRequest(
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
    ) = UpdateWardRequest(
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
    ) = WardResponse(
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
    ) = WardDetailResponse(
        wardNumber = wardNumber,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        officeLocation = officeLocation,
        officeLocationNepali = officeLocationNepali,
        municipality = municipality,
    )

    fun createWardSummaryResponse(
        wardNumber: Int = 1,
        population: Long = 1000L,
    ) = WardSummaryResponse(
        wardNumber = wardNumber,
        population = population,
    )
}
