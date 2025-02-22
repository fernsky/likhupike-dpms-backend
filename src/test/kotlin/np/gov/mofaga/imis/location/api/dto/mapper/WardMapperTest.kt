package np.gov.mofaga.imis.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import np.gov.mofaga.imis.location.api.dto.mapper.impl.WardMapperImpl
import np.gov.mofaga.imis.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.location.domain.Ward
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class WardMapperTest {
    private val municipalityMapper = mockk<MunicipalityMapper>()
    private lateinit var wardMapper: WardMapper
    private lateinit var testMunicipality: Municipality
    private lateinit var testWard: Ward

    @BeforeEach
    fun setup() {
        wardMapper = WardMapperImpl(municipalityMapper)
        testMunicipality = createTestMunicipality()
        testWard = createTestWard(testMunicipality)

        every {
            municipalityMapper.toSummaryResponse(any())
        } returns
            MunicipalitySummaryResponse(
                code = testMunicipality.code!!,
                name = testMunicipality.name!!,
                nameNepali = testMunicipality.nameNepali!!,
                type = testMunicipality.type!!,
                totalWards = testMunicipality.totalWards!!,
            )
    }

    @Test
    fun `should map Ward to WardResponse`() {
        // Act
        val response = wardMapper.toResponse(testWard)

        // Assert
        assertEquals(testWard.wardNumber, response.wardNumber)
        assertEquals(testWard.area, response.area)
        assertEquals(testWard.population, response.population)
        assertEquals(testWard.latitude, response.latitude)
        assertEquals(testWard.longitude, response.longitude)
        assertEquals(testWard.officeLocation, response.officeLocation)
        assertEquals(testWard.officeLocationNepali, response.officeLocationNepali)
        assertNotNull(response.municipality)
    }

    @Test
    fun `should map Ward to WardDetailResponse`() {
        // Act
        val response = wardMapper.toDetailResponse(testWard)

        // Assert
        assertEquals(testWard.wardNumber, response.wardNumber)
        assertEquals(testWard.area, response.area)
        assertEquals(testWard.population, response.population)
        assertEquals(testWard.latitude, response.latitude)
        assertEquals(testWard.longitude, response.longitude)
        assertEquals(testWard.officeLocation, response.officeLocation)
        assertEquals(testWard.officeLocationNepali, response.officeLocationNepali)
        assertNotNull(response.municipality)
    }

    @Test
    fun `should map Ward to WardSummaryResponse`() {
        // Act
        val response = wardMapper.toSummaryResponse(testWard)

        // Assert
        assertEquals(testWard.wardNumber, response.wardNumber)
        assertEquals(testWard.population, response.population)
    }

    private fun createTestMunicipality() =
        Municipality().apply {
            name = "Test Municipality"
            nameNepali = "परीक्षण नगरपालिका"
            code = "TEST-01"
            type = MunicipalityType.MUNICIPALITY
            area = BigDecimal("100.00")
            population = 10000L
            totalWards = 10
        }

    private fun createTestWard(municipality: Municipality) =
        Ward().apply {
            this.municipality = municipality
            wardNumber = 1
            area = BigDecimal("10.00")
            population = 1000L
            latitude = BigDecimal("27.7172")
            longitude = BigDecimal("85.3240")
            officeLocation = "Test Office"
            officeLocationNepali = "परीक्षण कार्यालय"
        }
}
