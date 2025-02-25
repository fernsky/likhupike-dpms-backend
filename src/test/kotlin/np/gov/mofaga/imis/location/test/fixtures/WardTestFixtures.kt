package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.enums.WardField
import np.gov.mofaga.imis.location.api.dto.request.CreateWardRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateWardRequest
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Ward
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.geojson.GeoJsonObject
import org.geojson.LngLatAlt
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import java.math.BigDecimal

object WardTestFixtures {
    private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)
    private val defaultGeometryConverter = GeometryConverter()

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

    fun createWardProjection(
        wardNumber: Int,
        municipalityCode: String = "TEST-M",
        fields: Set<WardField> = WardField.DEFAULT_FIELDS,
        includeGeometry: Boolean = false,
        geometryConverter: GeometryConverter = defaultGeometryConverter,
    ): DynamicWardProjection {
        val municipality = MunicipalityTestFixtures.createMunicipality(code = municipalityCode)
        val ward = createWard(municipality = municipality, wardNumber = wardNumber)
        val allFields = fields.toMutableSet()

        if (includeGeometry) {
            allFields.add(WardField.GEOMETRY)
            ward.geometry = createTestJTSPolygon()
        }

        return DynamicWardProjection.from(ward, allFields, geometryConverter)
    }

    fun createWardProjection(
        wardNumber: Int,
        fields: Set<WardField> = setOf(WardField.WARD_NUMBER),
        includeGeometry: Boolean = false,
    ): DynamicWardProjection {
        val ward = createWard(
            wardNumber = wardNumber,
            municipality = MunicipalityTestFixtures.createMunicipality()
        )

        if (includeGeometry) {
            ward.geometry = createTestJTSPolygon()
        }

        val allFields = fields.toMutableSet()
        if (includeGeometry) {
            allFields.add(WardField.GEOMETRY)
        }

        return DynamicWardProjection.from(ward, allFields, defaultGeometryConverter)
    }

    fun createSearchTestData(): List<Ward> =
        listOf(
            Ward().apply {
                wardNumber = 1
                municipality = MunicipalityTestFixtures.createMunicipality(code = "TEST-M1")
                population = 25000
                area = BigDecimal("2.5")
                geometry = createTestJTSPolygon()
            },
            Ward().apply {
                wardNumber = 2
                municipality = MunicipalityTestFixtures.createMunicipality(code = "TEST-M2")
                population = 18500
                area = BigDecimal("3.2")
                geometry = createTestJTSPolygon()
            },
            Ward().apply {
                wardNumber = 3
                municipality = MunicipalityTestFixtures.createMunicipality(code = "TEST-M3")
                population = 32000
                area = BigDecimal("4.1")
                geometry = createTestJTSPolygon()
            },
            Ward().apply {
                wardNumber = 4
                municipality = MunicipalityTestFixtures.createMunicipality(code = "TEST-M4")
                population = 15000
                area = BigDecimal("2.8")
                geometry = createTestJTSPolygon()
            },
        )

    private fun createTestJTSPolygon(): Polygon {
        // Similar to the existing createTestGeometry but returns JTS Polygon
        val coordinates =
            arrayOf(
                Coordinate(85.3240, 27.7172),
                Coordinate(85.3340, 27.7172),
                Coordinate(85.3340, 27.7272),
                Coordinate(85.3240, 27.7272),
                Coordinate(85.3240, 27.7172),
            )
        val ring = LinearRing(CoordinateArraySequence(coordinates), geometryFactory)
        return geometryFactory.createPolygon(ring)
    }
}
