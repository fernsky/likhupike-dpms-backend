package np.gov.mofaga.imis.location.api.dto.mapper

import np.gov.mofaga.imis.location.api.dto.mapper.impl.LocationSummaryMapperImpl
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("Location Summary Mapper Tests")
class LocationSummaryMapperTest {
    private lateinit var locationSummaryMapper: LocationSummaryMapper

    @BeforeEach
    fun setup() {
        locationSummaryMapper = LocationSummaryMapperImpl()
    }

    @Nested
    @DisplayName("District Summary Mapping Tests")
    inner class DistrictSummaryMappingTests {
        @Test
        @DisplayName("Should map district to summary response successfully")
        fun shouldMapDistrictToSummary() {
            // Given
            val district = DistrictTestFixtures.createDistrict()

            // When
            val response = locationSummaryMapper.toDistrictSummary(district)

            // Then
            assertNotNull(response)
            assertEquals(district.code, response.code)
            assertEquals(district.name, response.name)
            assertEquals(district.nameNepali, response.nameNepali)
            assertEquals(district.municipalities.size, response.municipalityCount)
        }

        @Test
        @DisplayName("Should throw exception when mapping district with null code")
        fun shouldThrowExceptionForNullCode() {
            // Given
            val district =
                DistrictTestFixtures.createDistrict().apply {
                    code = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toDistrictSummary(district)
                }
            assertEquals("District code cannot be null", exception.message)
        }

        @Test
        @DisplayName("Should throw exception when mapping district with null name")
        fun shouldThrowExceptionForNullName() {
            // Given
            val district =
                DistrictTestFixtures.createDistrict().apply {
                    name = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toDistrictSummary(district)
                }
            assertEquals("District name cannot be null", exception.message)
        }

        @Test
        @DisplayName("Should throw exception when mapping district with null Nepali name")
        fun shouldThrowExceptionForNullNepaliName() {
            // Given
            val district =
                DistrictTestFixtures.createDistrict().apply {
                    nameNepali = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toDistrictSummary(district)
                }
            assertEquals("District Nepali name cannot be null", exception.message)
        }
    }

    @Nested
    @DisplayName("Province Summary Mapping Tests")
    inner class ProvinceSummaryMappingTests {
        @Test
        @DisplayName("Should map province to summary response successfully")
        fun shouldMapProvinceToSummary() {
            // Given
            val province = ProvinceTestFixtures.createProvince()

            // When
            val response = locationSummaryMapper.toProvinceSummary(province)

            // Then
            assertNotNull(response)
            assertEquals(province.code, response.code)
            assertEquals(province.name, response.name)
            assertEquals(province.nameNepali, response.nameNepali)
        }

        @Test
        @DisplayName("Should throw exception when mapping province with null code")
        fun shouldThrowExceptionForNullCode() {
            // Given
            val province =
                ProvinceTestFixtures.createProvince().apply {
                    code = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toProvinceSummary(province)
                }
            assertEquals("Province code cannot be null", exception.message)
        }

        @Test
        @DisplayName("Should throw exception when mapping province with null name")
        fun shouldThrowExceptionForNullName() {
            // Given
            val province =
                ProvinceTestFixtures.createProvince().apply {
                    name = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toProvinceSummary(province)
                }
            assertEquals("Province name cannot be null", exception.message)
        }

        @Test
        @DisplayName("Should throw exception when mapping province with null Nepali name")
        fun shouldThrowExceptionForNullNepaliName() {
            // Given
            val province =
                ProvinceTestFixtures.createProvince().apply {
                    nameNepali = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toProvinceSummary(province)
                }
            assertEquals("Province Nepali name cannot be null", exception.message)
        }
    }

    @Nested
    @DisplayName("Municipality Summary Mapping Tests")
    inner class MunicipalitySummaryMappingTests {
        @Test
        @DisplayName("Should map municipality to summary response successfully")
        fun shouldMapMunicipalityToSummary() {
            // Given
            val municipality = MunicipalityTestFixtures.createMunicipality()

            // When
            val response = locationSummaryMapper.toMunicipalitySummary(municipality)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(municipality.code, code)
                assertEquals(municipality.name, name)
                assertEquals(municipality.nameNepali, nameNepali)
                assertEquals(municipality.type, type)
                assertEquals(municipality.totalWards ?: 0, totalWards)
            }
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null code")
        fun shouldThrowExceptionForNullCode() {
            // Given
            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    code = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toMunicipalitySummary(municipality)
                }
            assertEquals("Municipality code cannot be null", exception.message)
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null name")
        fun shouldThrowExceptionForNullName() {
            // Given
            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    name = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toMunicipalitySummary(municipality)
                }
            assertEquals("Municipality name cannot be null", exception.message)
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null Nepali name")
        fun shouldThrowExceptionForNullNepaliName() {
            // Given
            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    nameNepali = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toMunicipalitySummary(municipality)
                }
            assertEquals("Municipality Nepali name cannot be null", exception.message)
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null type")
        fun shouldThrowExceptionForNullType() {
            // Given
            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    type = null
                }

            // Then
            val exception =
                assertThrows<IllegalArgumentException> {
                    locationSummaryMapper.toMunicipalitySummary(municipality)
                }
            assertEquals("Municipality type cannot be null", exception.message)
        }
    }
}
