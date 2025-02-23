package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.request.CreateWardRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateWardRequest
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Ward
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import org.geojson.GeoJsonObject
import org.geojson.LngLatAlt
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
        geometry: GeometryRequest =
            GeometryRequest(
                type = "Polygon",
                coordinates =
                    listOf(
                        arrayOf(85.3240, 27.7172),
                        arrayOf(85.3245, 27.7172),
                        arrayOf(85.3245, 27.7177),
                        arrayOf(85.3240, 27.7177),
                        arrayOf(85.3240, 27.7172),
                    ),
            ),
    ) = CreateWardRequest(
        municipalityCode = municipalityCode,
        wardNumber = wardNumber,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        officeLocation = officeLocation,
        officeLocationNepali = officeLocationNepali,
        geometry = geometry,
    )

    fun createUpdateWardRequest(
        area: BigDecimal = BigDecimal("15.00"),
        population: Long = 1500L,
        latitude: BigDecimal = BigDecimal("27.7173"),
        longitude: BigDecimal = BigDecimal("85.3241"),
        officeLocation: String = "Updated Office",
        officeLocationNepali: String = "अद्यावधिक कार्यालय",
        geometry: GeometryRequest? =
            GeometryRequest(
                type = "Polygon",
                coordinates =
                    listOf(
                        arrayOf(85.3241, 27.7173),
                        arrayOf(85.3246, 27.7173),
                        arrayOf(85.3246, 27.7178),
                        arrayOf(85.3241, 27.7178),
                        arrayOf(85.3241, 27.7173),
                    ),
            ),
    ) = UpdateWardRequest(
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        officeLocation = officeLocation,
        officeLocationNepali = officeLocationNepali,
        geometry = geometry,
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
        geometry: GeoJsonObject? = createTestGeometry(),
    ) = WardDetailResponse(
        wardNumber = wardNumber,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        officeLocation = officeLocation,
        officeLocationNepali = officeLocationNepali,
        municipality = municipality,
        geometry = geometry,
    )

    fun createTestGeometry(): GeoJsonObject {
        val polygon = org.geojson.Polygon()

        // Create and set exterior ring first
        val exteriorRing =
            listOf(
                LngLatAlt(85.3240, 27.7172),
                LngLatAlt(85.3340, 27.7172),
                LngLatAlt(85.3340, 27.7272),
                LngLatAlt(85.3240, 27.7272),
                LngLatAlt(85.3240, 27.7172), // Close the ring
            )
        polygon.coordinates = mutableListOf(exteriorRing)

        // Add interior ring if needed
        val interiorRing =
            listOf(
                LngLatAlt(85.3270, 27.7192),
                LngLatAlt(85.3310, 27.7192),
                LngLatAlt(85.3310, 27.7252),
                LngLatAlt(85.3270, 27.7252),
                LngLatAlt(85.3270, 27.7192), // Close the ring
            )
        polygon.coordinates.add(interiorRing)

        return polygon
    }

    fun createWardSummaryResponse(
        wardNumber: Int = 1,
        population: Long = 1000L,
    ) = WardSummaryResponse(
        wardNumber = wardNumber,
        population = population,
    )
}
