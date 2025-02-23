package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.request.CreateMunicipalityRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import org.geojson.GeoJsonObject
import org.geojson.LngLatAlt
import java.math.BigDecimal

object MunicipalityTestFixtures {
    fun createMunicipality(
        district: District = DistrictTestFixtures.createDistrict(),
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका".uppercase(),
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
    ) = Municipality().apply {
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
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका".uppercase(),
        code: String = "TEST-M",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
        districtCode: String = "TEST-D",
        geometry: GeometryRequest =
            GeometryRequest(
                type = "Polygon",
                coordinates =
                    listOf(
                        arrayOf(85.3240, 27.7172),
                        arrayOf(85.3340, 27.7172),
                        arrayOf(85.3340, 27.7272),
                        arrayOf(85.3240, 27.7272),
                        arrayOf(85.3240, 27.7172),
                    ),
            ),
    ) = CreateMunicipalityRequest(
        name = name,
        nameNepali = nameNepali,
        code = code,
        type = type,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        totalWards = totalWards,
        districtCode = districtCode,
        geometry = geometry,
    )

    fun createUpdateMunicipalityRequest(
        name: String = "Updated Municipality",
        nameNepali: String = "अद्यावधिक नगरपालिका".uppercase(),
        area: BigDecimal = BigDecimal("150.75"),
        population: Long = 75000L,
        latitude: BigDecimal = BigDecimal("27.7173"),
        longitude: BigDecimal = BigDecimal("85.3241"),
        totalWards: Int = 15,
        geometry: GeometryRequest? =
            GeometryRequest(
                type = "Polygon",
                coordinates =
                    listOf(
                        arrayOf(85.3241, 27.7173),
                        arrayOf(85.3341, 27.7173),
                        arrayOf(85.3341, 27.7273),
                        arrayOf(85.3241, 27.7273),
                        arrayOf(85.3241, 27.7173),
                    ),
            ),
    ) = UpdateMunicipalityRequest(
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        latitude = latitude,
        longitude = longitude,
        totalWards = totalWards,
        geometry = geometry,
    )

    fun createMunicipalityResponse(
        code: String = "TEST-M",
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका".uppercase(),
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
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
        nameNepali: String = "परीक्षण नगरपालिका".uppercase(),
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        area: BigDecimal = BigDecimal("100.50"),
        population: Long = 50000L,
        latitude: BigDecimal = BigDecimal("27.7172"),
        longitude: BigDecimal = BigDecimal("85.3240"),
        totalWards: Int = 12,
        district: DistrictDetailResponse = DistrictTestFixtures.createDistrictDetailResponse(),
        geometry: GeoJsonObject? = createTestGeometry(),
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
        district = district,
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

    fun createMunicipalitySummaryResponse(
        code: String = "TEST-M",
        name: String = "Test Municipality",
        nameNepali: String = "परीक्षण नगरपालिका",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
        totalWards: Int = 12,
    ) = MunicipalitySummaryResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        type = type,
        totalWards = totalWards,
    )
}
