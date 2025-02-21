package np.gov.likhupikemun.dpms.location.service

import np.gov.likhupikemun.dpms.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.enums.MunicipalitySortField
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Municipality Service Integration Tests")
class MunicipalityServiceIntegrationTest {
    @Autowired
    private lateinit var municipalityService: MunicipalityService

    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    private lateinit var testProvince: Province
    private lateinit var testDistrict: District

    @BeforeEach
    fun setup() {
        municipalityRepository.deleteAll()
        districtRepository.deleteAll()
        provinceRepository.deleteAll()
        testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())
        testDistrict = districtRepository.save(DistrictTestFixtures.createDistrict(province = testProvince))
    }

    @Nested
    @DisplayName("Create Municipality Tests")
    inner class CreateMunicipalityTests {
        @Test
        @Transactional
        @DisplayName("Should create municipality successfully")
        fun shouldCreateMunicipality() {
            // Given
            val request =
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = testDistrict.code!!,
                )

            // When
            val result = municipalityService.createMunicipality(request)

            // Then
            assertNotNull(result)
            assertEquals(request.name, result.name)
            assertEquals(request.nameNepali, result.nameNepali)
            assertEquals(request.code, result.code)
            assertEquals(request.type, result.type)
            assertEquals(request.area, result.area)
            assertEquals(request.population, result.population)
            assertEquals(request.latitude, result.latitude)
            assertEquals(request.longitude, result.longitude)
            assertEquals(request.totalWards, result.totalWards)

            // Verify persistence
            val savedMunicipality = municipalityRepository.findByCodeIgnoreCase(result.code).orElseThrow()
            assertEquals(request.name, savedMunicipality.name)
            assertEquals(testDistrict.id, savedMunicipality.district?.id)
        }

        @Test
        @Transactional
        @DisplayName("Should throw exception for duplicate municipality code in same district")
        fun shouldThrowExceptionForDuplicateCode() {
            // Given
            val request =
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = testDistrict.code!!,
                    code = "TEST-M1",
                )
            municipalityService.createMunicipality(request)

            // When & Then
            assertThrows<DuplicateMunicipalityCodeException> {
                municipalityService.createMunicipality(request)
            }
        }
    }

    @Nested
    @DisplayName("Update Municipality Tests")
    inner class UpdateMunicipalityTests {
        @Test
        @Transactional
        @DisplayName("Should update municipality successfully")
        fun shouldUpdateMunicipality() {
            // Given
            val municipality =
                municipalityRepository.save(
                    MunicipalityTestFixtures.createMunicipality(district = testDistrict),
                )
            val updateRequest = MunicipalityTestFixtures.createUpdateMunicipalityRequest()

            // When
            val result = municipalityService.updateMunicipality(municipality.code!!, updateRequest)

            // Then
            assertNotNull(result)
            assertEquals(updateRequest.name, result.name)
            assertEquals(updateRequest.nameNepali, result.nameNepali)
            assertEquals(updateRequest.area, result.area)
            assertEquals(updateRequest.population, result.population)
            assertEquals(updateRequest.latitude, result.latitude)
            assertEquals(updateRequest.longitude, result.longitude)
            assertEquals(updateRequest.totalWards, result.totalWards)

            // Verify persistence
            val updatedMunicipality = municipalityRepository.findByCodeIgnoreCase(municipality.code!!).orElseThrow()
            assertEquals(updateRequest.name, updatedMunicipality.name)
        }
    }

    @Nested
    @DisplayName("Search Municipality Tests")
    inner class SearchMunicipalityTests {
        @Test
        @Transactional
        @DisplayName("Should search municipalities with criteria")
        fun shouldSearchWithCriteria() {
            // Given
            val municipalities =
                listOf(
                    MunicipalityTestFixtures.createMunicipality(
                        district = testDistrict,
                        name = "Test Municipality 1",
                        population = 50000,
                    ),
                    MunicipalityTestFixtures.createMunicipality(
                        district = testDistrict,
                        name = "Test Municipality 2",
                        population = 100000,
                    ),
                )
            municipalityRepository.saveAll(municipalities)

            val criteria =
                MunicipalitySearchCriteria(
                    searchTerm = "Test",
                    sortBy = MunicipalitySortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    page = 0,
                    pageSize = 10,
                )

            // When
            val result = municipalityService.searchMunicipalities(criteria)

            // Then
            assertEquals(2, result.totalElements)
            assertEquals("Test Municipality 2", result.content.first().name)
        }
    }

    @Nested
    @DisplayName("Geographic Search Tests")
    inner class GeographicSearchTests {
        @Test
        @Transactional
        @DisplayName("Should find nearby municipalities")
        fun shouldFindNearbyMunicipalities() {
            // Given
            val centralPoint = Pair(BigDecimal("27.7172"), BigDecimal("85.3240"))
            val municipality =
                municipalityRepository.save(
                    MunicipalityTestFixtures.createMunicipality(
                        district = testDistrict,
                        latitude = centralPoint.first,
                        longitude = centralPoint.second,
                    ),
                )

            // When
            val result =
                municipalityService.findNearbyMunicipalities(
                    latitude = centralPoint.first,
                    longitude = centralPoint.second,
                    radiusKm = 10.0,
                    page = 0,
                    size = 10,
                )

            // Then
            assertEquals(1, result.totalElements)
            assertEquals(municipality.name, result.content.first().name)
        }
    }
}
