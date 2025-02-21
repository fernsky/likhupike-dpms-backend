package np.gov.likhupikemun.dpms.location.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import np.gov.likhupikemun.dpms.location.api.dto.mapper.MunicipalityMapper
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.location.service.impl.MunicipalityServiceImpl
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("Municipality Service Tests")
class MunicipalityServiceTest {
    private val municipalityRepository = mockk<MunicipalityRepository>()
    private val districtRepository = mockk<DistrictRepository>()
    private val municipalityMapper = mockk<MunicipalityMapper>()
    private lateinit var municipalityService: MunicipalityService

    @BeforeEach
    fun setup() {
        municipalityService =
            MunicipalityServiceImpl(
                municipalityRepository = municipalityRepository,
                districtRepository = districtRepository,
                municipalityMapper = municipalityMapper,
            )
    }

    @Nested
    @DisplayName("Create Municipality Tests")
    inner class CreateMunicipalityTests {
        @Test
        @DisplayName("Should create municipality successfully")
        fun shouldCreateMunicipality() {
            // Given
            val request = MunicipalityTestFixtures.createMunicipalityRequest()
            val district = DistrictTestFixtures.createDistrict()
            val municipality = MunicipalityTestFixtures.createMunicipality()
            val expectedResponse = MunicipalityTestFixtures.createMunicipalityResponse()

            every {
                districtRepository.findByCodeIgnoreCase(request.districtCode)
            } returns Optional.of(district)

            every {
                municipalityRepository.existsByCodeAndDistrict(request.code, request.districtCode)
            } returns false

            every {
                municipalityRepository.save(any())
            } returns municipality

            every {
                municipalityMapper.toResponse(municipality)
            } returns expectedResponse

            // When
            val result = municipalityService.createMunicipality(request)

            // Then
            assertNotNull(result)
            assertEquals(expectedResponse, result)

            verify(exactly = 1) {
                districtRepository.findByCodeIgnoreCase(request.districtCode)
                municipalityRepository.existsByCodeAndDistrict(request.code, request.districtCode)
                municipalityRepository.save(any())
                municipalityMapper.toResponse(any())
            }
        }

        @Test
        @DisplayName("Should throw DistrictNotFoundException when district not found")
        fun shouldThrowDistrictNotFound() {
            // Given
            val request = MunicipalityTestFixtures.createMunicipalityRequest()

            every { districtRepository.findByCodeIgnoreCase(request.districtCode) } returns Optional.empty()

            // Then
            assertThrows<DistrictNotFoundException> {
                municipalityService.createMunicipality(request)
            }

            verify(exactly = 1) {
                districtRepository.findByCodeIgnoreCase(request.districtCode)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should throw DuplicateMunicipalityCodeException when code exists")
        fun shouldThrowDuplicateCode() {
            // Given
            val request = MunicipalityTestFixtures.createMunicipalityRequest()
            val district = DistrictTestFixtures.createDistrict()

            every { districtRepository.findByCodeIgnoreCase(request.districtCode) } returns Optional.of(district)
            every { municipalityRepository.existsByCodeAndDistrict(request.code, request.districtCode) } returns true

            // Then
            assertThrows<DuplicateMunicipalityCodeException> {
                municipalityService.createMunicipality(request)
            }

            verify(exactly = 1) {
                districtRepository.findByCodeIgnoreCase(request.districtCode)
                municipalityRepository.existsByCodeAndDistrict(request.code, request.districtCode)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }
    }

    @Nested
    @DisplayName("Update Municipality Tests")
    inner class UpdateMunicipalityTests {
        @Test
        @DisplayName("Should update municipality successfully")
        fun shouldUpdateMunicipality() {
            // Given
            val code = "TEST-M"
            val request = MunicipalityTestFixtures.createUpdateMunicipalityRequest()
            val existingMunicipality = MunicipalityTestFixtures.createMunicipality()
            val updatedMunicipality =
                existingMunicipality.copy().apply {
                    name = request.name
                    nameNepali = request.nameNepali
                    // ...update other fields...
                }
            val expectedResponse = MunicipalityTestFixtures.createMunicipalityResponse()

            every {
                municipalityRepository.findByCodeIgnoreCase(code)
            } returns Optional.of(existingMunicipality)

            every {
                municipalityRepository.save(any())
            } returns updatedMunicipality

            every {
                municipalityMapper.toResponse(updatedMunicipality)
            } returns expectedResponse

            // When
            val result = municipalityService.updateMunicipality(code, request)

            // Then
            assertNotNull(result)
            assertEquals(expectedResponse, result)

            verify(exactly = 1) {
                municipalityRepository.findByCodeIgnoreCase(code)
                municipalityRepository.save(any())
                municipalityMapper.toResponse(any())
            }
        }

        @Test
        @DisplayName("Should throw MunicipalityNotFoundException when municipality not found")
        fun shouldThrowMunicipalityNotFound() {
            // Given
            val code = "TEST-M"
            val request = MunicipalityTestFixtures.createUpdateMunicipalityRequest()

            every { municipalityRepository.findByCodeIgnoreCase(code) } returns Optional.empty()

            // Then
            assertThrows<MunicipalityNotFoundException> {
                municipalityService.updateMunicipality(code, request)
            }

            verify(exactly = 1) {
                municipalityRepository.findByCodeIgnoreCase(code)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should update only provided fields")
        fun shouldUpdateOnlyProvidedFields() {
            // Given
            val code = "TEST-M"
            val existingMunicipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    name = "Original Name"
                    code = "TEST001"
                    area = BigDecimal("100.00")
                    population = 50000
                    totalWards = 10
                }

            val request =
                MunicipalityTestFixtures.createUpdateMunicipalityRequest().apply {
                    name = "Updated Name"
                    population = 60000
                }

            val updatedMunicipality =
                existingMunicipality.copy().apply {
                    name = request.name
                    population = request.population
                }

            val expectedResponse = MunicipalityTestFixtures.createMunicipalityResponse()

            every { municipalityRepository.findByCodeIgnoreCase(code) } returns Optional.of(existingMunicipality)
            every { municipalityRepository.save(any()) } returns updatedMunicipality
            every { municipalityMapper.toResponse(updatedMunicipality) } returns expectedResponse

            // When
            val result = municipalityService.updateMunicipality(code, request)

            // Then
            assertNotNull(result)
            assertEquals(expectedResponse, result)

            verify(exactly = 1) {
                municipalityRepository.findByCodeIgnoreCase(code)
                municipalityRepository.save(any())
                municipalityMapper.toResponse(any())
            }
        }


        @Test
        @DisplayName("Should validate ward count when updating")
        fun shouldValidateWardCount() {
            // Given
            val code = "TEST-M"
            val existingMunicipality = MunicipalityTestFixtures.createMunicipality()
            val request =
                MunicipalityTestFixtures.createUpdateMunicipalityRequest().apply {
                    totalWards = 0
                }

            every { municipalityRepository.findByCodeIgnoreCase(code) } returns Optional.of(existingMunicipality)

            // Then
            assertThrows<InvalidMunicipalityDataException> {
                municipalityService.updateMunicipality(code, request)
            }

            verify(exactly = 1) {
                municipalityRepository.findByCodeIgnoreCase(code)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should validate coordinates when updating")
        fun shouldValidateCoordinates() {
            // Given
            val code = "TEST-M"
            val existingMunicipality = MunicipalityTestFixtures.createMunicipality()
            val request =
                MunicipalityTestFixtures.createUpdateMunicipalityRequest().apply {
                    latitude = BigDecimal("91.0") // Invalid latitude
                    longitude = BigDecimal("85.3240")
                }

            every { municipalityRepository.findByCodeIgnoreCase(code) } returns Optional.of(existingMunicipality)

            // Then
            assertThrows<InvalidLocationDataException> {
                municipalityService.updateMunicipality(code, request)
            }

            verify(exactly = 1) {
                municipalityRepository.findByCodeIgnoreCase(code)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }
    }

    @Nested
    @DisplayName("Search Municipality Tests")
    inner class SearchMunicipalityTests {
        @Test
        @DisplayName("Should search municipalities with basic criteria")
        fun shouldSearchWithBasicCriteria() {
            // Given
            val criteria = MunicipalityTestFixtures.createMunicipalitySearchCriteria()

            val municipality1 = MunicipalityTestFixtures.createMunicipality(name = "Test Municipality 1")
            val municipality2 = MunicipalityTestFixtures.createMunicipality(name = "Test Municipality 2")
            val mockPage = mockk<Page<Municipality>>()

            every { mockPage.content } returns listOf(municipality1, municipality2)
            every { mockPage.totalElements } returns 2
            every { mockPage.totalPages } returns 1
            every { mockPage.number } returns 0
            every { mockPage.size } returns 10

            every {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            } returns mockPage

            // When
            val result = municipalityService.searchMunicipalities(criteria)

            // Then
            assertNotNull(result)
            assertEquals(2, result.totalElements)
            assertEquals(2, result.content.size)
            verify(exactly = 1) {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            }
        }

        @Test
        @DisplayName("Should search with multiple filter criteria")
        fun shouldSearchWithMultipleFilters() {
            // Given
            val criteria =
                MunicipalityTestFixtures.createMunicipalitySearchCriteria().apply {
                    districtId = UUID.randomUUID()
                    types = setOf(MunicipalityType.MUNICIPALITY)
                    minWards = 5
                    maxWards = 15
                    minPopulation = 10000
                    maxPopulation = 50000
                    includeInactive = false
                }

            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    population = 25000
                    totalWards = 10
                    type = MunicipalityType.MUNICIPALITY
                }

            val mockPage = mockk<Page<Municipality>>()
            every { mockPage.content } returns listOf(municipality)
            every { mockPage.totalElements } returns 1
            every { mockPage.totalPages } returns 1
            every { mockPage.number } returns 0
            every { mockPage.size } returns 10

            every {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            } returns mockPage

            // When
            val result = municipalityService.searchMunicipalities(criteria)

            // Then
            assertNotNull(result)
            assertEquals(1, result.totalElements)
            with(result.content.first()) {
                assertEquals(municipality.name, name)
                assertEquals(municipality.population, population)
                assertEquals(municipality.totalWards, totalWards)
                assertEquals(municipality.type, type)
            }
        }

        @Test
        @DisplayName("Should search with geographic criteria")
        fun shouldSearchWithGeographicCriteria() {
            // Given
            val criteria =
                MunicipalityTestFixtures.createMunicipalitySearchCriteria().apply {
                    latitude = BigDecimal("27.7172")
                    longitude = BigDecimal("85.3240")
                    radiusKm = 10.0
                }

            val municipality =
                MunicipalityTestFixtures.createMunicipality().apply {
                    latitude = BigDecimal("27.7172")
                    longitude = BigDecimal("85.3240")
                }

            val mockPage = mockk<Page<Municipality>>()
            every { mockPage.content } returns listOf(municipality)
            every { mockPage.totalElements } returns 1
            every { mockPage.totalPages } returns 1
            every { mockPage.number } returns 0
            every { mockPage.size } returns 10

            every {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            } returns mockPage

            // When
            val result = municipalityService.searchMunicipalities(criteria)

            // Then
            assertNotNull(result)
            assertEquals(1, result.content.size)
            verify {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            }
        }

        @Test
        @DisplayName("Should return empty result for no matches")
        fun shouldReturnEmptyResult() {
            // Given
            val criteria =
                MunicipalityTestFixtures.createMunicipalitySearchCriteria().apply {
                    searchTerm = "nonexistent"
                }

            val mockPage = mockk<Page<Municipality>>()
            every { mockPage.content } returns emptyList()
            every { mockPage.totalElements } returns 0
            every { mockPage.totalPages } returns 0
            every { mockPage.number } returns 0
            every { mockPage.size } returns 10

            every {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            } returns mockPage

            // When
            val result = municipalityService.searchMunicipalities(criteria)

            // Then
            assertNotNull(result)
            assertEquals(0, result.totalElements)
            assertTrue(result.content.isEmpty())
        }

        @Test
        @DisplayName("Should validate search criteria")
        fun shouldValidateSearchCriteria() {
            // Given
            val invalidCriteria =
                MunicipalityTestFixtures.createMunicipalitySearchCriteria().apply {
                    minWards = 20
                    maxWards = 10 // Invalid: min > max
                }

            // Then
            assertThrows<IllegalArgumentException> {
                municipalityService.searchMunicipalities(invalidCriteria)
            }

            verify(exactly = 0) {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            }
        }

        @Test
        @DisplayName("Should sort results correctly")
        fun shouldSortResults() {
            // Given
            val criteria =
                MunicipalityTestFixtures.createMunicipalitySearchCriteria().apply {
                    sortBy = MunicipalitySortField.POPULATION
                    sortDirection = Sort.Direction.DESC
                }

            val municipality1 = MunicipalityTestFixtures.createMunicipality(name = "Municipality 1").apply { population = 50000 }
            val municipality2 = MunicipalityTestFixtures.createMunicipality(name = "Municipality 2").apply { population = 100000 }

            val mockPage = mockk<Page<Municipality>>()
            every { mockPage.content } returns listOf(municipality2, municipality1) // Sorted by population DESC
            every { mockPage.totalElements } returns 2
            every { mockPage.totalPages } returns 1
            every { mockPage.number } returns 0
            every { mockPage.size } returns 10

            every {
                municipalityRepository.findAll(any<Specification<Municipality>>(), any())
            } returns mockPage

            // When
            val result = municipalityService.searchMunicipalities(criteria)

            // Then
            assertNotNull(result)
            assertEquals(2, result.content.size)
            assertEquals(municipality2.population, result.content.first().population)
            assertEquals(municipality1.population, result.content.last().population)
        }
    }

    // Helper methods for creating test data
    private fun createTestMunicipality(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Municipality",
        code: String = "TEST001",
        type: MunicipalityType = MunicipalityType.MUNICIPALITY,
    ): Municipality =
        Municipality().apply {
            this.id = id
            this.name = name
            this.nameNepali = "टेस्ट नगरपालिका"
            this.code = code
            this.type = type
            
        }
}
