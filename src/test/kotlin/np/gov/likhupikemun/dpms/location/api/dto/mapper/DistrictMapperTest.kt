package np.gov.likhupikemun.dpms.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictStats
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Province
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("District Mapper Tests")
class DistrictMapperTest {
    private val provinceMapper = mockk<ProvinceMapper>()
    private val municipalityMapper = mockk<MunicipalityMapper>()
    private lateinit var districtMapper: DistrictMapper

    @BeforeEach
    fun setup() {
        districtMapper = DistrictMapperImpl(provinceMapper, municipalityMapper)
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
                provinceMapper.toSummaryResponse(district.province!!)
            } returns provinceSummary

            // When
            val response = districtMapper.toResponse(district)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(district.id, id)
                assertEquals(district.name, name)
                assertEquals(district.nameNepali, nameNepali)
                assertEquals(district.code, code)
                assertEquals(district.area, area)
                assertEquals(district.population, population)
                assertEquals(district.headquarter, headquarter)
                assertEquals(district.headquarterNepali, headquarterNepali)
                assertEquals(district.isActive, isActive)
                assertEquals(district.createdAt, createdAt)
                assertEquals(district.createdBy, createdBy)
                assertEquals(provinceSummary, province)
            }

            verify(exactly = 1) {
                provinceMapper.toSummaryResponse(district.province!!)
            }
        }

        @Test
        @DisplayName("Should throw exception when mapping district with null ID")
        fun shouldThrowExceptionForNullId() {
            // Given
            val district = createTestDistrict(id = null)

            // Then
            assertThrows<IllegalArgumentException> {
                districtMapper.toResponse(district)
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
        fun shouldMapToDetailedResponse() {
            // Given
            val district = createTestDistrict()
            val stats = createTestDistrictStats()

            every {
                provinceMapper.toDetailResponse(district.province!!)
            } returns createTestProvinceDetail()

            // When
            val response = districtMapper.toDetailResponse(district, stats)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(district.id, id)
                assertEquals(district.name, name)
                assertEquals(district.nameNepali, nameNepali)
                assertEquals(district.code, code)
                assertEquals(district.area, area)
                assertEquals(district.population, population)
                assertEquals(district.headquarter, headquarter)
                assertEquals(district.headquarterNepali, headquarterNepali)
                assertEquals(district.isActive, isActive)
                assertEquals(stats, stats)
            }
        }

        @Test
        @DisplayName("Should include municipality counts in detailed response")
        fun shouldIncludeMunicipalityCounts() {
            // Given
            val district =
                createTestDistrict().apply {
                    municipalities = createTestMunicipalities()
                }
            val stats = createTestDistrictStats()

            every {
                provinceMapper.toDetailResponse(district.province!!)
            } returns createTestProvinceDetail()

            // When
            val response = districtMapper.toDetailResponse(district, stats)

            // Then
            assertNotNull(response)
            assertEquals(3, response.totalMunicipalities)
            assertEquals(2, response.activeMunicipalities)
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
                assertEquals(district.id, id)
                assertEquals(district.name, name)
                assertEquals(district.nameNepali, nameNepali)
                assertEquals(district.code, code)
                assertEquals(district.isActive, isActive)
            }
        }
    }

    // Helper methods for creating test data
    private fun createTestDistrict(id: UUID? = UUID.randomUUID()): District {
        val province =
            Province().apply {
                this.id = UUID.randomUUID()
                name = "Test Province"
                nameNepali = "परीक्षण प्रदेश"
                code = "P1"
                isActive = true
            }

        return District().apply {
            this.id = id
            name = "Test District"
            nameNepali = "परीक्षण जिल्ला"
            code = "D1"
            area = BigDecimal("1000.50")
            population = 100000L
            headquarter = "Test HQ"
            headquarterNepali = "परीक्षण सदरमुकाम"
            isActive = true
            this.province = province
            createdAt = LocalDateTime.now()
            createdBy = "test-user"
        }
    }

    private fun createTestMunicipalities(): MutableSet<Municipality> =
        mutableSetOf(
            Municipality().apply { isActive = true },
            Municipality().apply { isActive = true },
            Municipality().apply { isActive = false },
        )

    private fun createTestProvinceSummary() =
        ProvinceSummaryResponse(
            id = UUID.randomUUID(),
            name = "Test Province",
            nameNepali = "परीक्षण प्रदेश",
            code = "P1",
            isActive = true,
        )

    private fun createTestProvinceDetail() = createTestProvinceSummary()

    private fun createTestDistrictStats() =
        DistrictStats(
            id = UUID.randomUUID(),
            totalMunicipalities = 10,
            activeMunicipalities = 8,
            totalPopulation = 500000L,
            totalArea = BigDecimal("5000.00"),
            demographicBreakdown =
                mapOf(
                    "Category1" to 100000L,
                    "Category2" to 150000L,
                ),
            infrastructureStats =
                mapOf(
                    "Roads" to BigDecimal("500.00"),
                    "Schools" to BigDecimal("100.00"),
                ),
        )
}
