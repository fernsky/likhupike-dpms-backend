package np.gov.mofaga.imis.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.mofaga.imis.shared.util.GeometryConverter
import np.gov.mofaga.imis.location.api.dto.mapper.impl.MunicipalityMapperImpl
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.geojson.GeoJsonObject

@DisplayName("Municipality Mapper Tests")
class MunicipalityMapperTest {
    private val districtMapper = mockk<DistrictMapper>()
    private val geometryConverter = mockk<GeometryConverter>()
    private lateinit var municipalityMapper: MunicipalityMapper
    private val mockGeoJson = mockk<GeoJsonObject>()

    @BeforeEach
    fun setup() {
        municipalityMapper = MunicipalityMapperImpl(districtMapper, geometryConverter)

        every {
            geometryConverter.convertToGeoJson(any())
        } returns mockGeoJson
    }

    @Nested
    @DisplayName("Basic Response Mapping Tests")
    inner class BasicResponseMappingTests {
        @Test
        @DisplayName("Should map municipality to response successfully")
        fun shouldMapToResponse() {
            // Given
            val municipality = MunicipalityTestFixtures.createMunicipality()
            val expectedDistrictResponse = DistrictTestFixtures.createDistrictSummaryResponse()
            val expectedResponse =
                MunicipalityTestFixtures.createMunicipalityResponse(
                    district = expectedDistrictResponse,
                )

            every {
                districtMapper.toSummaryResponse(municipality.district!!)
            } returns expectedDistrictResponse

            // When
            val response = municipalityMapper.toResponse(municipality)

            // Then
            assertNotNull(response)
            assertEquals(expectedResponse.code, response.code)
            assertEquals(expectedResponse.name, response.name)
            assertEquals(expectedResponse.nameNepali, response.nameNepali)
            assertEquals(expectedResponse.type, response.type)
            assertEquals(expectedResponse.area, response.area)
            assertEquals(expectedResponse.population, response.population)
            assertEquals(expectedResponse.district, response.district)

            verify(exactly = 1) {
                districtMapper.toSummaryResponse(municipality.district!!)
            }
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null required fields")
        fun shouldThrowExceptionForNullRequiredFields() {
            // Given
            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    code = null // Make required field null
                }

            // Then
            assertThrows<IllegalArgumentException> {
                municipalityMapper.toResponse(municipality)
            }
        }
    }

    @Nested
    @DisplayName("Detail Response Mapping Tests")
    inner class DetailResponseMappingTests {
        @Test
        @DisplayName("Should map municipality to detailed response successfully")
        fun shouldMapToDetailResponse() {
            // Given
            val municipality = MunicipalityTestFixtures.createMunicipality()
            val expectedDistrictResponse = DistrictTestFixtures.createDistrictDetailResponse()
            val expectedResponse =
                MunicipalityTestFixtures.createMunicipalityDetailResponse(
                    district = expectedDistrictResponse,
                )

            every {
                districtMapper.toDetailResponse(municipality.district!!)
            } returns expectedDistrictResponse

            // When
            val response = municipalityMapper.toDetailResponse(municipality)

            // Then
            assertNotNull(response)
            assertEquals(expectedResponse.code, response.code)
            assertEquals(expectedResponse.name, response.name)
            assertEquals(expectedResponse.nameNepali, response.nameNepali)
            assertEquals(expectedResponse.type, response.type)
            assertEquals(expectedResponse.district, response.district)

            verify(exactly = 1) {
                districtMapper.toDetailResponse(municipality.district!!)
            }
        }
    }

    @Nested
    @DisplayName("Summary Response Mapping Tests")
    inner class SummaryResponseMappingTests {
        @Test
        @DisplayName("Should map municipality to summary response successfully")
        fun shouldMapToSummaryResponse() {
            // Given
            val municipality = MunicipalityTestFixtures.createMunicipality()
            val expectedResponse = MunicipalityTestFixtures.createMunicipalitySummaryResponse()

            // When
            val response = municipalityMapper.toSummaryResponse(municipality)

            // Then
            assertNotNull(response)
            assertEquals(expectedResponse.code, response.code)
            assertEquals(expectedResponse.name, response.name)
            assertEquals(expectedResponse.nameNepali, response.nameNepali)
            assertEquals(expectedResponse.type, response.type)
            assertEquals(expectedResponse.totalWards, response.totalWards)
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null required fields")
        fun shouldThrowExceptionForNullRequiredFields() {
            // Given
            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    type = null // Make required field null
                }

            // Then
            assertThrows<IllegalArgumentException> {
                municipalityMapper.toSummaryResponse(municipality)
            }
        }
    }
}
