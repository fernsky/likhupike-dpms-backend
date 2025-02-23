package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.request.CreateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import org.geojson.GeoJsonObject
import org.geojson.LngLatAlt
import java.math.BigDecimal

object ProvinceTestFixtures {
    private var counter = 0L

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

    private fun createDistrictSummaries(): List<DistrictSummaryResponse> =
        (1..3).map { index ->
            DistrictSummaryResponse(
                code = "DIST-$index",
                name = "Test District $index",
                nameNepali = "परीक्षण जिल्ला $index",
                municipalityCount = 5,
            )
        }
}
