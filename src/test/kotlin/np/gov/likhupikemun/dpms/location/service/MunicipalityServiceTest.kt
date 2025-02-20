package np.gov.likhupikemun.dpms.location.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.exception.DistrictNotFoundException
import np.gov.likhupikemun.dpms.location.exception.DuplicateMunicipalityCodeException
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.shared.service.CurrentUserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("Municipality Service Tests")
class MunicipalityServiceTest {
    private val municipalityRepository = mockk<MunicipalityRepository>()
    private val districtRepository = mockk<DistrictRepository>()
    private val currentUserService = mockk<CurrentUserService>()
    private lateinit var municipalityService: MunicipalityService

    @BeforeEach
    fun setup() {
        municipalityService =
            MunicipalityServiceImpl(
                municipalityRepository = municipalityRepository,
                districtRepository = districtRepository,
                currentUserService = currentUserService,
            )
    }

    @Nested
    @DisplayName("Create Municipality Tests")
    inner class CreateMunicipalityTests {
        @Test
        @DisplayName("Should create municipality successfully")
        fun shouldCreateMunicipality() {
            // Given
            val districtId = UUID.randomUUID()
            val district =
                District().apply {
                    id = districtId
                    name = "Test District"
                }

            val request =
                CreateMunicipalityRequest(
                    name = "Test Municipality",
                    nameNepali = "टेस्ट नगरपालिका",
                    code = "TEST001",
                    type = MunicipalityType.MUNICIPALITY,
                    area = BigDecimal("100.50"),
                    population = 50000,
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                    totalWards = 12,
                    districtId = districtId,
                )

            val municipalitySlot = slot<Municipality>()

            every { districtRepository.findByIdOrNull(districtId) } returns district
            every { municipalityRepository.existsByCodeAndDistrict(any(), any(), any()) } returns false
            every { municipalityRepository.save(capture(municipalitySlot)) } answers { municipalitySlot.captured }

            // When
            val result = municipalityService.createMunicipality(request)

            // Then
            assertNotNull(result)
            assertEquals(request.name, result.name)
            assertEquals(request.code, result.code)
            assertEquals(request.type, result.type)

            verify(exactly = 1) {
                districtRepository.findByIdOrNull(districtId)
                municipalityRepository.existsByCodeAndDistrict(request.code, districtId, null)
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should throw DistrictNotFoundException when district not found")
        fun shouldThrowDistrictNotFound() {
            // Given
            val districtId = UUID.randomUUID()
            val request =
                CreateMunicipalityRequest(
                    name = "Test Municipality",
                    nameNepali = "टेस्ट नगरपालिका",
                    code = "TEST001",
                    type = MunicipalityType.MUNICIPALITY,
                    districtId = districtId,
                    totalWards = 12,
                )

            every { districtRepository.findByIdOrNull(districtId) } returns null

            // Then
            assertThrows<DistrictNotFoundException> {
                municipalityService.createMunicipality(request)
            }

            verify(exactly = 1) {
                districtRepository.findByIdOrNull(districtId)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should throw DuplicateMunicipalityCodeException when code exists")
        fun shouldThrowDuplicateCode() {
            // Given
            val districtId = UUID.randomUUID()
            val district =
                District().apply {
                    id = districtId
                    name = "Test District"
                }

            val request =
                CreateMunicipalityRequest(
                    name = "Test Municipality",
                    nameNepali = "टेस्ट नगरपालिका",
                    code = "TEST001",
                    type = MunicipalityType.MUNICIPALITY,
                    districtId = districtId,
                    totalWards = 12,
                )

            every { districtRepository.findByIdOrNull(districtId) } returns district
            every { municipalityRepository.existsByCodeAndDistrict(request.code, districtId, null) } returns true

            // Then
            assertThrows<DuplicateMunicipalityCodeException> {
                municipalityService.createMunicipality(request)
            }

            verify(exactly = 1) {
                districtRepository.findByIdOrNull(districtId)
                municipalityRepository.existsByCodeAndDistrict(request.code, districtId, null)
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
            val municipalityId = UUID.randomUUID()
            val existingMunicipality = createTestMunicipality(id = municipalityId)
            val request =
                UpdateMunicipalityRequest(
                    name = "Updated Municipality",
                    nameNepali = "अपडेटेड नगरपालिका",
                    area = BigDecimal("200.50"),
                    population = 75000,
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                    totalWards = 15,
                    isActive = true,
                )

            every { municipalityRepository.findById(municipalityId) } returns Optional.of(existingMunicipality)
            every { municipalityRepository.save(any()) } answers { firstArg() }

            // When
            val result = municipalityService.updateMunicipality(municipalityId, request)

            // Then
            assertNotNull(result)
            assertEquals(request.name, result.name)
            assertEquals(request.nameNepali, result.nameNepali)
            assertEquals(request.area, result.area)
            assertEquals(request.population, result.population)
            assertEquals(request.totalWards, result.totalWards)

            verify(exactly = 1) {
                municipalityRepository.findById(municipalityId)
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should throw MunicipalityNotFoundException when municipality not found")
        fun shouldThrowMunicipalityNotFound() {
            // Given
            val municipalityId = UUID.randomUUID()
            val request =
                UpdateMunicipalityRequest(
                    name = "Updated Municipality",
                    nameNepali = "अपडेटेड नगरपालिका",
                )

            every { municipalityRepository.findById(municipalityId) } returns Optional.empty()

            // Then
            assertThrows<MunicipalityNotFoundException> {
                municipalityService.updateMunicipality(municipalityId, request)
            }

            verify(exactly = 1) {
                municipalityRepository.findById(municipalityId)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should update only provided fields")
        fun shouldUpdateOnlyProvidedFields() {
            // Given
            val municipalityId = UUID.randomUUID()
            val existingMunicipality =
                createTestMunicipality(
                    id = municipalityId,
                    name = "Original Name",
                    code = "TEST001",
                ).apply {
                    area = BigDecimal("100.00")
                    population = 50000
                    totalWards = 10
                }

            val request =
                UpdateMunicipalityRequest(
                    name = "Updated Name",
                    population = 60000,
                )

            every { municipalityRepository.findById(municipalityId) } returns Optional.of(existingMunicipality)
            every { municipalityRepository.save(any()) } answers { firstArg() }

            // When
            val result = municipalityService.updateMunicipality(municipalityId, request)

            // Then
            assertNotNull(result)
            assertEquals(request.name, result.name)
            assertEquals(existingMunicipality.nameNepali, result.nameNepali)
            assertEquals(existingMunicipality.area, result.area)
            assertEquals(request.population, result.population)
            assertEquals(existingMunicipality.totalWards, result.totalWards)

            verify(exactly = 1) {
                municipalityRepository.findById(municipalityId)
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should handle deactivation through update")
        fun shouldHandleDeactivation() {
            // Given
            val municipalityId = UUID.randomUUID()
            val existingMunicipality = createTestMunicipality(id = municipalityId)
            val request = UpdateMunicipalityRequest(isActive = false)

            // Mock no active wards
            existingMunicipality.wards = mutableSetOf()

            every { municipalityRepository.findById(municipalityId) } returns Optional.of(existingMunicipality)
            every { municipalityRepository.save(any()) } answers { firstArg() }

            // When
            val result = municipalityService.updateMunicipality(municipalityId, request)

            // Then
            assertNotNull(result)
            assertEquals(false, result.isActive)

            verify(exactly = 1) {
                municipalityRepository.findById(municipalityId)
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should validate ward count when updating")
        fun shouldValidateWardCount() {
            // Given
            val municipalityId = UUID.randomUUID()
            val existingMunicipality = createTestMunicipality(id = municipalityId)
            val request = UpdateMunicipalityRequest(totalWards = 0)

            every { municipalityRepository.findById(municipalityId) } returns Optional.of(existingMunicipality)

            // Then
            assertThrows<InvalidMunicipalityDataException> {
                municipalityService.updateMunicipality(municipalityId, request)
            }

            verify(exactly = 1) {
                municipalityRepository.findById(municipalityId)
            }
            verify(exactly = 0) {
                municipalityRepository.save(any())
            }
        }

        @Test
        @DisplayName("Should validate coordinates when updating")
        fun shouldValidateCoordinates() {
            // Given
            val municipalityId = UUID.randomUUID()
            val existingMunicipality = createTestMunicipality(id = municipalityId)
            val request =
                UpdateMunicipalityRequest(
                    latitude = BigDecimal("91.0"), // Invalid latitude
                    longitude = BigDecimal("85.3240"),
                )

            every { municipalityRepository.findById(municipalityId) } returns Optional.of(existingMunicipality)

            // Then
            assertThrows<InvalidLocationDataException> {
                municipalityService.updateMunicipality(municipalityId, request)
            }

            verify(exactly = 1) {
                municipalityRepository.findById(municipalityId)
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
            val criteria =
                MunicipalitySearchCriteria(
                    searchTerm = "test",
                    page = 0,
                    pageSize = 10,
                )

            val municipality1 = createTestMunicipality(name = "Test Municipality 1")
            val municipality2 = createTestMunicipality(name = "Test Municipality 2")
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
            val districtId = UUID.randomUUID()
            val criteria =
                MunicipalitySearchCriteria(
                    searchTerm = "test",
                    districtId = districtId,
                    types = setOf(MunicipalityType.MUNICIPALITY),
                    minWards = 5,
                    maxWards = 15,
                    minPopulation = 10000,
                    maxPopulation = 50000,
                    includeInactive = false,
                )

            val municipality =
                createTestMunicipality().apply {
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
                MunicipalitySearchCriteria(
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                    radiusKm = 10.0,
                )

            val municipality =
                createTestMunicipality().apply {
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
            val criteria = MunicipalitySearchCriteria(searchTerm = "nonexistent")

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
                MunicipalitySearchCriteria(
                    minWards = 20,
                    maxWards = 10, // Invalid: min > max
                )

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
                MunicipalitySearchCriteria(
                    sortBy = MunicipalitySortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                )

            val municipality1 = createTestMunicipality(name = "Municipality 1").apply { population = 50000 }
            val municipality2 = createTestMunicipality(name = "Municipality 2").apply { population = 100000 }

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
            this.isActive = true
        }
}
