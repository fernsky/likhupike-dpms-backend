package np.gov.mofaga.imis.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.mofaga.imis.shared.util.GeometryConverter
import np.gov.mofaga.imis.location.api.dto.mapper.impl.ProvinceMapperImpl
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.geojson.GeoJsonObject

@DisplayName("Province Mapper Tests")
class ProvinceMapperTest {
    private val locationSummaryMapper = mockk<LocationSummaryMapper>()
    private val geometryConverter = mockk<GeometryConverter>()
    private lateinit var provinceMapper: ProvinceMapper
    private val mockGeoJson = mockk<GeoJsonObject>()

    @BeforeEach
    fun setup() {
        provinceMapper = ProvinceMapperImpl(locationSummaryMapper, geometryConverter)

        every {
            geometryConverter.convertToGeoJson(any())
        } returns mockGeoJson
    }

    @Nested
    @DisplayName("Basic Response Mapping Tests")
    inner class BasicResponseMappingTests {
        @Test
        fun `should map province to response successfully`() {
            // Given
            val province = ProvinceTestFixtures.createProvince()
            addTestDistricts(province)

            // When
            val response = provinceMapper.toResponse(province)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(province.code, code)
                assertEquals(province.name, name)
                assertEquals(province.nameNepali, nameNepali)
                assertEquals(province.area, area)
                assertEquals(province.population, population)
                assertEquals(province.headquarter, headquarter)
                assertEquals(province.headquarterNepali, headquarterNepali)
                assertEquals(3, districtCount) // Changed from 2 to 3
                assertEquals(40000L, totalPopulation)
                assertEquals(BigDecimal("400.00"), totalArea)
            }
        }

        @Test
        fun `should throw exception when mapping province with null required fields`() {
            // Given
            val province = Province()

            // Then
            assertThrows<IllegalArgumentException> {
                provinceMapper.toResponse(province)
            }
        }
    }

    @Nested
    @DisplayName("Detailed Response Mapping Tests")
    inner class DetailedResponseMappingTests {
        @Test
        fun `should map province to detailed response successfully`() {
            // Given
            val province = ProvinceTestFixtures.createProvince()
            val districts = addTestDistricts(province)

            districts.forEach { district ->
                every {
                    locationSummaryMapper.toDistrictSummary(district)
                } returns
                    DistrictTestFixtures.createDistrictSummaryResponse(
                        code = district.code!!,
                        name = district.name!!,
                        nameNepali = district.nameNepali!!,
                        municipalityCount = district.municipalities.size,
                    )
            }

            // When
            val response = provinceMapper.toDetailResponse(province)

            // Then
            assertNotNull(response)
            with(response) {
                assertEquals(province.code, code)
                assertEquals(province.name, name)
                assertEquals(province.nameNepali, nameNepali)
                assertEquals(province.area, area)
                assertEquals(province.population, population)
                assertEquals(province.headquarter, headquarter)
                assertEquals(province.headquarterNepali, headquarterNepali)
                assertEquals(3, districts.size)
            }

            verify(exactly = 3) {
                locationSummaryMapper.toDistrictSummary(any())
            }
        }
    }

    @Nested
    @DisplayName("Summary Response Mapping Tests")
    inner class SummaryResponseMappingTests {
        @Test
        fun `should map province to summary response successfully`() {
            // Given
            val province = ProvinceTestFixtures.createProvince()
            val expectedSummary =
                ProvinceTestFixtures.createProvinceSummaryResponse(
                    code = province.code!!,
                    name = province.name!!,
                    nameNepali = province.nameNepali!!,
                )

            every {
                locationSummaryMapper.toProvinceSummary(province)
            } returns expectedSummary

            // When
            val response = provinceMapper.toSummaryResponse(province)

            // Then
            assertNotNull(response)
            assertEquals(province.code, response.code)
            assertEquals(province.name, response.name)
            assertEquals(province.nameNepali, response.nameNepali) // Fixed: was comparing with response.name

            verify(exactly = 1) {
                locationSummaryMapper.toProvinceSummary(province)
            }
        }
    }

    private fun addTestDistricts(province: Province): List<District> {
        val districts =
            listOf(
                createTestDistrict(province, population = 15000L, area = BigDecimal("150.00")),
                createTestDistrict(province, population = 25000L, area = BigDecimal("250.00")),
                createTestDistrict(province, population = 0L, area = BigDecimal("0.00")),
            )
        province.districts.addAll(districts)
        return districts
    }

    private fun createTestDistrict(
        province: Province,
        population: Long,
        area: BigDecimal,
    ): District =
        DistrictTestFixtures.createDistrict(
            province = province,
            population = population,
            area = area,
        )
}
