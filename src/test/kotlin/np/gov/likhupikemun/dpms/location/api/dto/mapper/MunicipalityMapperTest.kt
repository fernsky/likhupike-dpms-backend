package np.gov.likhupikemun.dpms.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictSummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityStats
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
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

@DisplayName("Municipality Mapper Tests")
class MunicipalityMapperTest {
    private val districtMapper = mockk<DistrictMapper>()
    private lateinit var municipalityMapper: MunicipalityMapper

    @BeforeEach
    fun setup() {
        municipalityMapper = MunicipalityMapperImpl(districtMapper)
    }

    @Nested
    @DisplayName("Basic Response Mapping Tests")
    inner class BasicResponseMappingTests {
        @Test
        @DisplayName("Should map municipality to response successfully")
        fun shouldMapToResponse() {
            // Given
            val municipality = createTestMunicipality()
            val districtSummary = createTestDistrictSummary()

            every {
                districtMapper.toSummaryResponse(municipality.district!!)
            } returns districtSummary

            // When
            val response = municipalityMapper.toResponse(municipality)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(municipality.id, id)
                assertEquals(municipality.name, name)
                assertEquals(municipality.nameNepali, nameNepali)
                assertEquals(municipality.code, code)
                assertEquals(municipality.type, type)
                assertEquals(municipality.area, area)
                assertEquals(municipality.population, population)
                assertEquals(municipality.latitude, latitude)
                assertEquals(municipality.longitude, longitude)
                assertEquals(municipality.totalWards, totalWards)
                assertEquals(municipality.isActive, isActive)
                assertEquals(municipality.createdAt, createdAt)
                assertEquals(municipality.createdBy, createdBy)
                assertEquals(districtSummary, district)
            }

            verify(exactly = 1) {
                districtMapper.toSummaryResponse(municipality.district!!)
            }
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null ID")
        fun shouldThrowExceptionForNullId() {
            // Given
            val municipality = createTestMunicipality(id = null)

            // Then
            assertThrows<IllegalArgumentException> {
                municipalityMapper.toResponse(municipality)
            }
        }

        @Test
        @DisplayName("Should throw exception when mapping municipality with null required fields")
        fun shouldThrowExceptionForNullRequiredFields() {
            // Given
            val municipality = Municipality()

            // Then
            assertThrows<IllegalArgumentException> {
                municipalityMapper.toResponse(municipality)
            }
        }
    }

    @Nested
    @DisplayName("Detailed Response Mapping Tests")
    inner class DetailedResponseMappingTests {
        @Test
        @DisplayName("Should map municipality to detailed response successfully")
        fun shouldMapToDetailedResponse() {
            // Given
            val municipality = createTestMunicipality()
            val districtDetail = createTestDistrictSummary()
            val stats = createTestMunicipalityStats()

            every {
                districtMapper.toDetailResponse(municipality.district!!)
            } returns districtDetail

            // When
            val response = municipalityMapper.toDetailResponse(municipality, stats)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(municipality.id, id)
                assertEquals(municipality.name, name)
                assertEquals(municipality.nameNepali, nameNepali)
                assertEquals(municipality.code, code)
                assertEquals(municipality.type, type)
                assertEquals(municipality.area, area)
                assertEquals(municipality.population, population)
                assertEquals(municipality.totalWards, totalWards)
                assertEquals(municipality.isActive, isActive)
                assertEquals(stats, stats)
                assertEquals(districtDetail, district)
            }

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
            val municipality = createTestMunicipality()

            // When
            val response = municipalityMapper.toSummaryResponse(municipality)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(municipality.id, id)
                assertEquals(municipality.name, name)
                assertEquals(municipality.nameNepali, nameNepali)
                assertEquals(municipality.code, code)
                assertEquals(municipality.type, type)
                assertEquals(municipality.totalWards, totalWards)
                assertEquals(municipality.isActive, isActive)
            }
        }
    }

    // Helper methods
    private fun createTestMunicipality(id: UUID? = UUID.randomUUID()): Municipality {
        val district =
            District().apply {
                this.id = UUID.randomUUID()
                name = "Test District"
                nameNepali = "परीक्षण जिल्ला"
                code = "TEST-D"
                isActive = true
            }

        return Municipality().apply {
            this.id = id
            name = "Test Municipality"
            nameNepali = "परीक्षण नगरपालिका"
            code = "TEST-M"
            type = MunicipalityType.MUNICIPALITY
            area = BigDecimal("100.50")
            population = 50000L
            latitude = BigDecimal("27.7172")
            longitude = BigDecimal("85.3240")
            totalWards = 12
            isActive = true
            this.district = district
            createdAt = LocalDateTime.now()
            createdBy = "test-user"
        }
    }

    private fun createTestDistrictSummary() =
        DistrictSummaryResponse(
            id = UUID.randomUUID(),
            name = "Test District",
            nameNepali = "परीक्षण जिल्ला",
            code = "TEST-D",
            isActive = true,
        )

    private fun createTestMunicipalityStats() =
        MunicipalityStats(
            totalWards = 12,
            activeWards = 10,
            totalPopulation = 50000L,
            totalArea = BigDecimal("100.50"),
            totalFamilies = 10000L,
            wardStats = emptyList(),
        )
}
