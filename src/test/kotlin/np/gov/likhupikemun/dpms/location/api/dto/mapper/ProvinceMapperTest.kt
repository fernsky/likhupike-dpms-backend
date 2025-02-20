package np.gov.likhupikemun.dpms.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.likhupikemun.dpms.location.api.dto.mapper.impl.ProvinceMapperImpl
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictSummaryResponse
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
                assertEquals(province.id, id)
                assertEquals(province.name, name)
                assertEquals(province.nameNepali, nameNepali)
                assertEquals(province.code, code)
                assertEquals(province.area, area)
                assertEquals(province.population, population)
                assertEquals(province.headquarter, headquarter)
                assertEquals(province.headquarterNepali, headquarterNepali)
                assertEquals(province.isActive, isActive)
                assertEquals(2, districtCount) // Only active districts
                assertTrue(totalPopulation > 0)
                assertTrue(totalArea > BigDecimal.ZERO)
            }
        }

        @Test
        fun `should throw exception when mapping province with null ID`() {
            // Given
            val province = ProvinceTestFixtures.createProvince(id = null)

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
        fun `should map province to detailed response with districts`() {
            // Given
            val province = ProvinceTestFixtures.createProvince()
            val districts = addTestDistricts(province)
            val districtSummaries = createDistrictSummaries(districts)

            districts.forEach { district ->
                every { 
                    districtMapper.toSummaryResponse(district) 
                } returns districtSummaries.find { it.id == district.id }!!
            }

            // When
            val response = provinceMapper.toDetailResponse(province)

            // Then
            assertNotNull(response)
            assertEquals(province.id, response.id)
            assertEquals(3, response.districts.size)
            
            with(response.stats) {
                assertEquals(2, totalDistricts) // Only active districts
                assertEquals(4, totalMunicipalities) // Only active municipalities
                assertTrue(totalPopulation > 0)
                assertTrue(totalArea > BigDecimal.ZERO)
                assertTrue(populationDensity > BigDecimal.ZERO)
                assertEquals(2, municipalityTypes.size)
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
        fun `should map province to summary response`() {
            // Given
            val province = ProvinceTestFixtures.createProvince()

            // When
            val response = provinceMapper.toSummaryResponse(province)

            // Then
            assertNotNull(response)
            assertEquals(province.id, response.id)
            assertEquals(province.name, response.name)
            assertEquals(province.nameNepali, response.nameNepali)
            assertEquals(province.code, response.code)
        }
    }

    @Nested
    @DisplayName("Statistics Calculation Tests")
    inner class StatisticsCalculationTests {

        @Test
        fun `should calculate correct statistics for province`() {
            // Given
            val province = ProvinceTestFixtures.createProvince()
            addTestDistricts(province)

            // When
            val response = provinceMapper.toDetailResponse(province)

            // Then
            with(response.stats) {
                assertEquals(2, totalDistricts)
                assertEquals(4, totalMunicipalities)
                assertEquals(40000L, totalPopulation)
                assertEquals(BigDecimal("400.00"), totalArea)
                assertEquals(BigDecimal("100.00"), populationDensity)
                assertEquals(2, municipalityTypes.size)
                assertEquals(2, municipalityTypes["MUNICIPALITY"])
                assertEquals(2, municipalityTypes["RURAL_MUNICIPALITY"])
            }
        }
    }

    private fun addTestDistricts(province: Province): List<District> {
        val districts = listOf(
            createTestDistrict(province, isActive = true),
            createTestDistrict(province, isActive = true),
            createTestDistrict(province, isActive = false)
        )
        province.districts.addAll(districts)
        return districts
    }

    private fun createTestDistrict(province: Province, isActive: Boolean): District {
        val district = DistrictTestFixtures.createDistrict(province = province)
        district.isActive = isActive
        
        if (isActive) {
            addTestMunicipalities(district)
        }
        
        return district
    }

    private fun addTestMunicipalities(district: District) {
        val municipalities = listOf(
            createTestMunicipality(district, MunicipalityType.MUNICIPALITY, true),
            createTestMunicipality(district, MunicipalityType.RURAL_MUNICIPALITY, true),
            createTestMunicipality(district, MunicipalityType.MUNICIPALITY, false)
        )
        district.municipalities.addAll(municipalities)
    }

    private fun createTestMunicipality(
        district: District,
        type: MunicipalityType,
        isActive: Boolean
    ): Municipality {
        return Municipality().apply {
            id = UUID.randomUUID()
            this.district = district
            this.type = type
            this.isActive = isActive
            population = 10000L
            area = BigDecimal("100.00")
        }
    }

    private fun createDistrictSummaries(districts: List<District>): List<DistrictSummaryResponse> {
        return districts.map { district ->
            DistrictSummaryResponse(
                id = district.id!!,
                name = district.name!!,
                nameNepali = district.nameNepali!!,
                code = district.code!!,
                isActive = district.isActive,
                municipalityCount = district.municipalities.count { it.isActive }
            )
        }
    }
}
