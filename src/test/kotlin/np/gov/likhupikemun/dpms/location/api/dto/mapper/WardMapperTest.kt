package np.gov.likhupikemun.dpms.location.api.dto.mapper

import io.mockk.every
import io.mockk.mockk
import np.gov.likhupikemun.dpms.location.api.dto.mapper.impl.WardMapperImpl
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.WardStats
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.domain.Ward
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mapstruct.factory.Mappers
import java.math.BigDecimal
import java.util.*

class WardMapperTest {
    private val wardMapper: WardMapper = Mappers.getMapper(WardMapper::class.java)
    private lateinit var testMunicipality: Municipality
    private lateinit var testWard: Ward

    @BeforeEach
    fun setup() {
        testMunicipality = createTestMunicipality()
        testWard = createTestWard(testMunicipality)
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
        assertEquals(testMunicipality.code, response.municipality.code)
        assertEquals(testMunicipality.name, response.municipality.name)
        assertEquals(testMunicipality.nameNepali, response.municipality.nameNepali)
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
        assertEquals(testMunicipality.code, response.municipality.code)
        assertEquals(testMunicipality.name, response.municipality.name)
        assertEquals(testMunicipality.nameNepali, response.municipality.nameNepali)
    }

    @Test
    fun `should map Ward to WardSummaryResponse`() {
        // Act
        val response = wardMapper.toSummaryResponse(testWard)

        // Assert
        assertEquals(testWard.wardNumber, response.wardNumber)
        assertEquals(testWard.population, response.population)
    }

    @Test
    fun `should map CreateWardRequest to Ward`() {
        // Arrange
        val request = CreateWardRequest(
            municipalityCode = testMunicipality.code,
            wardNumber = 1,
            area = BigDecimal("10.00"),
            population = 1000L,
            latitude = BigDecimal("27.7172"),
            longitude = BigDecimal("85.3240"),
            officeLocation = "Test Office",
            officeLocationNepali = "परीक्षण कार्यालय"
        )

        // Act
        val ward = wardMapper.toEntity(request, testMunicipality)

        // Assert
        assertEquals(request.wardNumber, ward.wardNumber)
        assertEquals(request.area, ward.area)
        assertEquals(request.population, ward.population)
        assertEquals(request.latitude, ward.latitude)
        assertEquals(request.longitude, ward.longitude)
        assertEquals(request.officeLocation, ward.officeLocation)
        assertEquals(request.officeLocationNepali, ward.officeLocationNepali)
        assertEquals(testMunicipality.code, ward.municipality.code)
    }

    private fun createTestMunicipality() = Municipality().apply {
        name = "Test Municipality"
        nameNepali = "परीक्षण नगरपालिका"
        code = "TEST-01"
        type = MunicipalityType.MUNICIPALITY
        area = BigDecimal("100.00")
        population = 10000L
        totalWards = 10
        isActive = true
    }

    private fun createTestWard(municipality: Municipality) = Ward().apply {
        this.municipality = municipality
        wardNumber = 1
        area = BigDecimal("10.00")
        population = 1000L
        latitude = BigDecimal("27.7172")
        longitude = BigDecimal("85.3240")
        officeLocation = "Test Office"
        officeLocationNepali = "परीक्षण कार्यालय"
        isActive = true
    }
}
