package np.gov.likhupikemun.dpms.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.likhupikemun.dpms.location.api.dto.mapper.impl.DistrictMapperImpl
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("District Mapper Tests")
class DistrictMapperTest {
    private val locationSummaryMapper = mockk<LocationSummaryMapper>()
    private val municipalityMapper = mockk<MunicipalityMapper>()
    private lateinit var districtMapper: DistrictMapper

    @BeforeEach
    fun setup() {
        districtMapper = DistrictMapperImpl(locationSummaryMapper, municipalityMapper)
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
                municipalityMapper.toSummaryResponse(any())
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
                municipalityMapper.toSummaryResponse(any())
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

            // When
            val response = districtMapper.toSummaryResponse(district)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(district.code, code)
                assertEquals(district.name, name)
                assertEquals(district.nameNepali, nameNepali)
                assertEquals(0, municipalityCount)
            }
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
        }
    }

    private fun createTestProvinceSummary() =
        ProvinceSummaryResponse(
            code = "P1",
            name = "Test Province",
            nameNepali = "परीक्षण प्रदेश",
        )

    private fun createTestProvinceDetail() =
        ProvinceDetailResponse(
            code = "P1",
            name = "Test Province",
            nameNepali = "परीक्षण प्रदेश",
            area = BigDecimal("1000.00"),
            population = 1000000L,
            headquarter = "Test HQ",
            headquarterNepali = "परीक्षण सदरमुकाम",
            districts = emptyList(),
        )
}
