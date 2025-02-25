package np.gov.mofaga.imis.location.test.fixtures

import np.gov.mofaga.imis.location.api.dto.enums.DistrictField
import np.gov.mofaga.imis.location.api.dto.request.CreateDistrictRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateDistrictRequest
import np.gov.mofaga.imis.location.api.dto.response.*
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.geojson.GeoJsonObject
import org.geojson.LngLatAlt
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LinearRing
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.geom.impl.CoordinateArraySequence
import java.math.BigDecimal

object DistrictTestFixtures {
    private val geometryFactory = GeometryFactory()

    private val defaultGeometryConverter = GeometryConverter()

    fun createDistrict(
        code: String = "D1",
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला",
        area: BigDecimal = BigDecimal("100.00"),
        population: Long = 10000L,
        headquarter: String = "Test HQ",
        headquarterNepali: String = "परीक्षण सदरमुकाम",
        province: Province = ProvinceTestFixtures.createProvince(),
        geometry: Polygon = createTestJTSPolygon(),
    ): District =
        District().apply {
            this.code = code
            this.name = name
            this.nameNepali = nameNepali
            this.area = area
            this.population = population
            this.headquarter = headquarter
            this.headquarterNepali = headquarterNepali
            this.province = province
            this.geometry = geometry
        }

    private fun createTestJTSPolygon(): Polygon {
        val coordinates =
            arrayOf(
                Coordinate(85.0000, 27.0000),
                Coordinate(85.5000, 27.0000),
                Coordinate(85.5000, 27.5000),
                Coordinate(85.0000, 27.5000),
                Coordinate(85.0000, 27.0000), // Close the ring
            )

        val ring = LinearRing(CoordinateArraySequence(coordinates), geometryFactory)
        return geometryFactory.createPolygon(ring)
    }

    fun createDistrictRequest(
        name: String = "Test District",
        nameNepali: String = "परीक्षण जिल्ला".uppercase(),
        code: String = "TEST-D",
        provinceCode: String = "TEST-P",
        area: BigDecimal = BigDecimal("1000.50"),
        population: Long = 100000L,
        headquarter: String = "TEST HEADQUARTER",
        headquarterNepali: String = "परीक्षण सदरमुकाम".uppercase(),
        geometry: GeometryRequest =
            GeometryRequest(
                type = "Polygon",
                coordinates =
                    listOf(
                        arrayOf(85.0000, 27.0000),
                        arrayOf(85.5000, 27.0000),
                        arrayOf(85.5000, 27.5000),
                        arrayOf(85.0000, 27.5000),
                        arrayOf(85.0000, 27.0000),
                    ),
            ),
    ) = CreateDistrictRequest(
        name = name,
        nameNepali = nameNepali,
        code = code,
        provinceCode = provinceCode,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        geometry = geometry,
    )

    fun createUpdateDistrictRequest(
        name: String = "UPDATED DISTRICT",
        nameNepali: String = "अद्यावधिक जिल्ला".uppercase(),
        area: BigDecimal = BigDecimal("1500.75"),
        population: Long = 150000L,
        headquarter: String = "UPDATED HEADQUARTER",
        headquarterNepali: String = "अद्यावधिक सदरमुकाम",
        geometry: GeometryRequest? =
            GeometryRequest(
                type = "Polygon",
                coordinates =
                    listOf(
                        arrayOf(85.0001, 27.0001),
                        arrayOf(85.5001, 27.0001),
                        arrayOf(85.5001, 27.5001),
                        arrayOf(85.0001, 27.5001),
                        arrayOf(85.0001, 27.0001),
                    ),
            ),
    ) = UpdateDistrictRequest(
        name = name,
        nameNepali = nameNepali,
        area = area,
        population = population,
        headquarter = headquarter,
        headquarterNepali = headquarterNepali,
        geometry = geometry,
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
        geometry: GeoJsonObject? = createTestGeometry(),
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
        geometry = geometry,
    )

    fun createTestGeometry(): GeoJsonObject {
        val polygon = org.geojson.Polygon()

        // Create and set exterior ring first
        val exteriorRing =
            listOf(
                LngLatAlt(85.0000, 27.0000),
                LngLatAlt(85.5000, 27.0000),
                LngLatAlt(85.5000, 27.5000),
                LngLatAlt(85.0000, 27.5000),
                LngLatAlt(85.0000, 27.0000), // Close the ring
            )
        polygon.coordinates = mutableListOf(exteriorRing)

        // Add interior ring if needed
        val interiorRing =
            listOf(
                LngLatAlt(85.1000, 27.1000),
                LngLatAlt(85.4000, 27.1000),
                LngLatAlt(85.4000, 27.4000),
                LngLatAlt(85.1000, 27.4000),
                LngLatAlt(85.1000, 27.1000), // Close the ring
            )
        polygon.coordinates.add(interiorRing)

        return polygon
    }

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
        geometry = createTestGeometry(),
    )

    private fun createTestMunicipalities(district: District) =
        listOf(
            MunicipalityTestFixtures.createMunicipality(
                district = district,
                code = "${district.code}-M1",
                name = "Test Municipality 1",
            ),
            MunicipalityTestFixtures.createMunicipality(
                district = district,
                code = "${district.code}-M2",
                name = "Test Municipality 2",
            ),
            MunicipalityTestFixtures.createMunicipality(
                district = district,
                code = "${district.code}-M3",
                name = "Test Municipality 3",
            ),
        )

    fun createDistrictProjection(
        code: String,
        fields: Set<DistrictField> = DistrictField.DEFAULT_FIELDS,
        includeTotals: Boolean = false,
        includeGeometry: Boolean = false,
        includeMunicipalities: Boolean = false,
        geometryConverter: GeometryConverter = defaultGeometryConverter,
    ): DynamicDistrictProjection {
        val district = createDistrict(code = code)
        val allFields = fields.toMutableSet()

        if (includeTotals) {
            allFields.addAll(
                setOf(
                    DistrictField.TOTAL_AREA,
                    DistrictField.TOTAL_POPULATION,
                    DistrictField.MUNICIPALITY_COUNT,
                ),
            )
        }
        if (includeGeometry) {
            allFields.add(DistrictField.GEOMETRY)
            district.geometry = createTestJTSPolygon()
        }
        if (includeMunicipalities) {
            allFields.add(DistrictField.MUNICIPALITIES)
            district.municipalities = createTestMunicipalities(district).toMutableSet()
        }

        return DynamicDistrictProjection.from(district, allFields, geometryConverter)
    }

    fun createSearchTestData(province: Province? = null): List<District> {
        val testProvince = province ?: ProvinceTestFixtures.createProvince()

        return listOf(
            District().apply {
                name = "Kathmandu"
                nameNepali = "काठमाडौं"
                code = "KTM"
                population = 2017532
                area = BigDecimal("395.0")
                geometry = createTestJTSPolygon()
                this.province = testProvince
            },
            District().apply {
                name = "Lalitpur"
                nameNepali = "ललितपुर"
                code = "LTP"
                population = 468132
                area = BigDecimal("385.0")
                geometry = createTestJTSPolygon()
                this.province = testProvince
            },
            District().apply {
                name = "Bhaktapur"
                nameNepali = "भक्तपुर"
                code = "BKT"
                population = 304651
                area = BigDecimal("119.0")
                geometry = createTestJTSPolygon()
                this.province = testProvince
            },
            District().apply {
                name = "Chitwan"
                nameNepali = "चितवन"
                code = "CHT"
                population = 579984
                area = BigDecimal("2218.0")
                geometry = createTestJTSPolygon()
                this.province = testProvince
            },
        )
    }
}
