package np.gov.likhupikemun.dpms.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.likhupikemun.dpms.location.api.dto.mapper.impl.ProvinceMapperImpl
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("Province Mapper Tests")
class ProvinceMapperTest {

    private val districtMapper = mockk<DistrictMapper>()
    private lateinit var provinceMapper: ProvinceMapper

    @BeforeEach
    fun setup() {
        provinceMapper = ProvinceMapperImpl(districtMapper)
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
                assertEquals(2, districtCount)
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
                    districtMapper.toSummaryResponse(district) 
                } returns DistrictTestFixtures.createDistrictSummaryResponse(
                    code = district.code!!,
                    name = district.name!!,
                    nameNepali = district.nameNepali!!,
                    municipalityCount = district.municipalities.size
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
                districtMapper.toSummaryResponse(any()) 
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

            // When
            val response = provinceMapper.toSummaryResponse(province)

            // Then
            assertNotNull(response)
            assertEquals(province.code, response.code)
            assertEquals(province.name, response.name)
            assertEquals(province.nameNepali, response.nameNepali)
        }
    }

    private fun addTestDistricts(province: Province): List<District> {
        val districts = listOf(
            createTestDistrict(province, population = 15000L, area = BigDecimal("150.00")),
            createTestDistrict(province, population = 25000L, area = BigDecimal("250.00")),
            createTestDistrict(province, population = 0L, area = BigDecimal("0.00"))
        )
        province.districts.addAll(districts)
        return districts
    }

    private fun createTestDistrict(
        province: Province,
        population: Long,
        area: BigDecimal
    ): District = DistrictTestFixtures.createDistrict(
        province = province,
        population = population,
        area = area
    )
}
