package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.api.dto.request.CreateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.geojson.GeoJsonObject
import org.geojson.LngLatAlt
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import java.math.BigDecimal

object ProvinceTestFixtures {
    private var counter = 0L
    private val geometryFactory = GeometryFactory(PrecisionModel(), 4326)

    // Add a default geometry converter for testing
    private val defaultGeometryConverter = GeometryConverter()

    private fun generateUniqueCode(): String {
        val timestamp = System.currentTimeMillis()
        val count = counter++
        return "TEST-P-$timestamp-$count"
    }

    fun createProvince(
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
        code: String = generateUniqueCode(),
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
    ): Province =
        Province().apply {
            this.name = name
            this.nameNepali = nameNepali
            this.code = code
            this.area = area
            this.population = population
            this.headquarter = headquarter
            this.headquarterNepali = headquarterNepali
        }

    fun createProvinceRequest(
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
        code: String = generateUniqueCode(),
        area: BigDecimal = BigDecimal("10000.50"),
        population: Long = 500000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        geometry: GeometryRequest =
            GeometryRequest(
                type = "polygon",
                coordinates =
                    listOf(
                        arrayOf(85.0, 27.0),
                        arrayOf(85.5, 27.0),
                        arrayOf(85.5, 27.5),
                        arrayOf(85.0, 27.5),
                    ),
            ),
    ) = CreateProvinceRequest(
        name = name,
        nameNepali = nameNepali,
        code = code.toUpperCase(),
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        geometry = geometry,
    )

    fun createUpdateProvinceRequest(
        name: String = "Updated Province",
        nameNepali: String = "अद्यावधिक प्रदेश",
        area: BigDecimal = BigDecimal("12000.75"),
        population: Long = 600000L,
        headquarter: String = "Updated Headquarter",
        headquarterNepali: String = "अद्यावधिक सदरमुकाम",
        geometry: GeometryRequest? =
            GeometryRequest(
                type = "polygon",
                coordinates =
                    listOf(
                        arrayOf(85.0, 27.0),
                        arrayOf(85.5, 27.0),
                        arrayOf(85.5, 27.5),
                        arrayOf(85.0, 27.5),
                    ),
            ),
    ) = UpdateProvinceRequest(
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        geometry = geometry,
    )

    fun createProvinceResponse(
        code: String = generateUniqueCode(),
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
        code: String = generateUniqueCode(),
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
        area: BigDecimal = BigDecimal("10000.50"),
        population: Long = 500000L,
        headquarter: String = "Test Headquarter",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        districts: List<DistrictSummaryResponse> = createDistrictSummaries(),
        geometry: GeoJsonObject? = createTestGeometry(),
    ) = ProvinceDetailResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        districts = districts,
        geometry = geometry,
    )

    fun createTestGeometry(): GeoJsonObject {
        val polygon = org.geojson.Polygon()

        // Create and set exterior ring first
        val exteriorRing =
            listOf(
                LngLatAlt(85.3, 27.7),
                LngLatAlt(85.4, 27.7),
                LngLatAlt(85.4, 27.8),
                LngLatAlt(85.3, 27.8),
                LngLatAlt(85.3, 27.7), // Close the ring
            )
        polygon.coordinates = mutableListOf(exteriorRing)

        // Add interior ring if needed
        val interiorRing =
            listOf(
                LngLatAlt(85.35, 27.72),
                LngLatAlt(85.38, 27.72),
                LngLatAlt(85.38, 27.75),
                LngLatAlt(85.35, 27.75),
                LngLatAlt(85.35, 27.72), // Close the ring
            )
        polygon.coordinates.add(interiorRing)

        return polygon
    }

    fun createProvinceSummaryResponse(
        code: String = generateUniqueCode(),
        name: String = "Test Province",
        nameNepali: String = "परीक्षण प्रदेश",
    ) = ProvinceSummaryResponse(
        code = code,
        name = name,
        nameNepali = nameNepali,
    )

    fun createDistrictSummaries(): List<DistrictSummaryResponse> =
        (1..3).map { index ->
            DistrictSummaryResponse(
                code = "DIST-$index",
                name = "Test District $index",
                nameNepali = "परीक्षण जिल्ला $index",
                municipalityCount = 5,
            )
        }

    // Add helper method to create test districts
    private fun createTestDistricts(province: Province? = null): MutableSet<np.gov.mofaga.imis.location.domain.District> {
        val testProvince = province ?: createProvince(code = "TEST-P-PARENT")
        return mutableSetOf(
            DistrictTestFixtures.createDistrict(
                code = "TEST-D1",
                province = testProvince,
                name = "Test District 1",
                nameNepali = "परीक्षण जिल्ला १",
                area = BigDecimal("500.00"),
                population = 50000L,
            ),
        )
    }

    fun createProvinceProjection(
        code: String,
        fields: Set<ProvinceField> = ProvinceField.DEFAULT_FIELDS,
        includeTotals: Boolean = false,
        includeGeometry: Boolean = false,
        includeDistricts: Boolean = false,
        geometryConverter: GeometryConverter = defaultGeometryConverter,
    ): DynamicProvinceProjection {
        val province = createProvince(code = code)
        val allFields = fields.toMutableSet()

        if (includeTotals) {
            allFields.addAll(
                setOf(
                    ProvinceField.TOTAL_AREA,
                    ProvinceField.TOTAL_POPULATION,
                    ProvinceField.TOTAL_MUNICIPALITIES,
                    ProvinceField.DISTRICT_COUNT,
                ),
            )
        }
        if (includeGeometry) {
            allFields.add(ProvinceField.GEOMETRY)
            province.geometry = createSampleGeometry(27.0, 85.0)
        }
        if (includeDistricts) {
            allFields.add(ProvinceField.DISTRICTS)
            province.districts = createTestDistricts(province) // Pass the province
        }

        return DynamicProvinceProjection.from(province, allFields, geometryConverter)
    }

    fun createSearchTestData(): List<Province> =
        listOf(
            Province().apply {
                name = "Bagmati Province"
                nameNepali = "बागमती प्रदेश" // Make sure this is set
                code = "P3"
                population = 6084042
                area = BigDecimal("20300.0")
                geometry = createSampleGeometry(27.0, 85.0)
            },
            Province().apply {
                name = "Gandaki Province"
                nameNepali = "गण्डकी प्रदेश"
                code = "P4"
                area = BigDecimal("21504.00")
                population = 2403757L
            },
            Province().apply {
                name = "Lumbini Province"
                nameNepali = "लुम्बिनी प्रदेश"
                code = "P5"
                area = BigDecimal("22288.00")
                population = 4499272L
            },
        )

    private fun createSampleGeometry(
        lat: Double,
        lon: Double,
    ) = geometryFactory.createPolygon(
        arrayOf(
            Coordinate(lon, lat),
            Coordinate(lon + 1, lat),
            Coordinate(lon + 1, lat + 1),
            Coordinate(lon, lat + 1),
            Coordinate(lon, lat),
        ),
    )
}
