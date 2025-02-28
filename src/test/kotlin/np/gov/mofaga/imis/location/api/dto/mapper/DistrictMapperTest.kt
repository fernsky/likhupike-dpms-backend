package np.gov.mofaga.imis.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.mofaga.imis.location.api.dto.mapper.impl.DistrictMapperImpl
import np.gov.mofaga.imis.location.api.dto.response.DistrictSummaryResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceSummaryResponse
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.shared.util.GeometryConverter
import org.geojson.GeoJsonObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.locationtech.jts.geom.Polygon
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("District Mapper Tests")
class DistrictMapperTest {
    private lateinit var districtMapper: DistrictMapper
    private lateinit var locationSummaryMapper: LocationSummaryMapper
    private val geometryConverter = mockk<GeometryConverter>()
    private val mockGeoJson = mockk<GeoJsonObject>()
    private val mockGeometry = mockk<Polygon>()

    @BeforeEach
    fun setup() {
        locationSummaryMapper = mockk<LocationSummaryMapper>()
        districtMapper = DistrictMapperImpl(locationSummaryMapper, geometryConverter)

        every {
            geometryConverter.convertToGeoJson(any())
        } returns mockGeoJson
    }

    @Nested
    @DisplayName("Basic Response Mapping Tests")
    inner class BasicResponseMappingTests {
        @Test
        @DisplayName("Should map district to response successfully")
        fun shouldMapToResponse() {
            // Given
            val district = createTestDistrict()
            val provinceSummary = createTestProvinceSummary()

            every {
                locationSummaryMapper.toProvinceSummary(district.province!!)
            } returns provinceSummary

            // When
            val response = districtMapper.toResponse(district)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(district.code, code)
                assertEquals(district.name, name)
                assertEquals(district.nameNepali, nameNepali)
                assertEquals(district.area, area)
                assertEquals(district.population, population)
                assertEquals(district.headquarter, headquarter)
                assertEquals(district.headquarterNepali, headquarterNepali)
                assertEquals(provinceSummary, province)
                assertEquals(0, municipalityCount)
            }

            verify(exactly = 1) {
                locationSummaryMapper.toProvinceSummary(district.province!!)
            }
        }

        @Test
        @DisplayName("Should throw exception when mapping district with null required fields")
        fun shouldThrowExceptionForNullRequiredFields() {
            // Given
            val district = District()

            // Then
            assertThrows<IllegalArgumentException> {
                districtMapper.toResponse(district)
            }
        }
    }

    @Nested
    @DisplayName("Detailed Response Mapping Tests")
    inner class DetailedResponseMappingTests {
        @Test
        @DisplayName("Should map district to detailed response successfully")
        fun shouldMapToDetailResponse() {
            // Given
            val district = createTestDistrict()
            val provinceSummary = createTestProvinceSummary()

            every {
                locationSummaryMapper.toProvinceSummary(district.province!!)
            } returns provinceSummary

            // When
            val response = districtMapper.toDetailResponse(district)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(district.code, code)
                assertEquals(district.name, name)
                assertEquals(district.nameNepali, nameNepali)
                assertEquals(district.area, area)
                assertEquals(district.population, population)
                assertEquals(district.headquarter, headquarter)
                assertEquals(district.headquarterNepali, headquarterNepali)
                assertEquals(provinceSummary, province)
                assertEquals(0, municipalities.size)
            }

            verify(exactly = 1) {
                locationSummaryMapper.toProvinceSummary(district.province!!)
            }
        }

        @Test
        @DisplayName("Should include municipalities in detailed response")
        fun shouldIncludeMunicipalities() {
            // Given
            val district =
                createTestDistrict().apply {
                    addMunicipality(MunicipalityTestFixtures.createMunicipality(district = this))
                    addMunicipality(MunicipalityTestFixtures.createMunicipality(district = this))
                }
            val provinceSummary = createTestProvinceSummary()

            every {
                locationSummaryMapper.toProvinceSummary(district.province!!)
            } returns provinceSummary

            every {
                locationSummaryMapper.toMunicipalitySummary(any())
            } returns MunicipalityTestFixtures.createMunicipalitySummaryResponse()

            // When
            val response = districtMapper.toDetailResponse(district)

            // Then
            assertNotNull(response)
            assertEquals(2, response.municipalities.size)

            verify(exactly = 1) {
                locationSummaryMapper.toProvinceSummary(district.province!!)
            }
            verify(exactly = 2) {
                locationSummaryMapper.toMunicipalitySummary(any())
            }
        }
    }

    @Nested
    @DisplayName("Summary Response Mapping Tests")
    inner class SummaryResponseMappingTests {
        @Test
        @DisplayName("Should map district to summary response successfully")
        fun shouldMapToSummaryResponse() {
            // Given
            val district = createTestDistrict()
            val expectedSummary =
                DistrictSummaryResponse(
                    code = district.code!!,
                    name = district.name!!,
                    nameNepali = district.nameNepali!!,
                    municipalityCount = district.municipalities.size,
                )

            // Setup mock behavior
            every { locationSummaryMapper.toDistrictSummary(district) } returns expectedSummary

            // When
            val result = districtMapper.toSummaryResponse(district)

            // Then
            assertNotNull(result)
            assertEquals(expectedSummary.code, result.code)
            assertEquals(expectedSummary.name, result.name)
            assertEquals(expectedSummary.nameNepali, result.nameNepali)
            assertEquals(expectedSummary.municipalityCount, result.municipalityCount)

            // Verify the mock was called
            verify(exactly = 1) { locationSummaryMapper.toDistrictSummary(district) }
        }
    }

    // Helper methods for creating test data
    private fun createTestDistrict(): District {
        val province =
            Province().apply {
                name = "Test Province"
                nameNepali = "परीक्षण प्रदेश"
                code = "P1"
            }

        return District().apply {
            name = "Test District"
            nameNepali = "परीक्षण जिल्ला"
            code = "D1"
            area = BigDecimal("1000.50")
            population = 100000L
            headquarter = "Test HQ"
            headquarterNepali = "परीक्षण सदरमुकाम"
            this.province = province
            this.geometry = mockGeometry // Using the class-level mockGeometry property
        }
    }

    private fun createTestProvinceSummary() =
        ProvinceSummaryResponse(
            code = "P1",
            name = "Test Province",
            nameNepali = "परीक्षण प्रदेश",
        )
}
