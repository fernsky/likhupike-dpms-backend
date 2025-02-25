package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.mofaga.imis.location.api.dto.enums.MunicipalityField
import np.gov.mofaga.imis.location.api.dto.enums.MunicipalitySortField
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.repository.DistrictRepository
import np.gov.mofaga.imis.location.repository.MunicipalityRepository
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@WebMvcTest(MunicipalityService::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Municipality Search Integration Tests")
@WithMockUser(roles = ["SUPER_ADMIN"])
class MunicipalitySearchIntegrationTest {
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
        setupTestData()
    }

    private fun setupTestData() {
        testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())
        testDistrict = districtRepository.save(DistrictTestFixtures.createDistrict(province = testProvince))
        MunicipalityTestFixtures.createSearchTestData().forEach { municipality ->
            municipality.district = testDistrict
            municipalityRepository.save(municipality)
        }
    }

    @Nested
    @DisplayName("Dynamic Projection Tests")
    inner class DynamicProjectionTests {
        @Test
        @Transactional
        fun `should return only requested fields`() {
            val criteria =
                MunicipalitySearchCriteria(
                    fields = setOf(MunicipalityField.NAME, MunicipalityField.TYPE),
                    searchTerm = "Kathmandu",
                )

            val result = municipalityService.searchMunicipalities(criteria)
            val municipality = result.content.first()

            assertNotNull(municipality.getValue(MunicipalityField.NAME))
            assertNotNull(municipality.getValue(MunicipalityField.TYPE))
            assertNull(municipality.getValue(MunicipalityField.AREA))
        }
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    inner class AdvancedSearchTests {
        @Test
        @Transactional
        fun `should search with geometry included`() {
            val criteria =
                MunicipalitySearchCriteria(
                    fields = setOf(MunicipalityField.NAME, MunicipalityField.GEOMETRY),
                    includeGeometry = true,
                )

            val result = municipalityService.searchMunicipalities(criteria)
            val municipality = result.content.first()

            assertNotNull(municipality.getValue(MunicipalityField.GEOMETRY))
        }

        @Test
        @Transactional
        fun `should sort by population in descending order`() {
            val criteria =
                MunicipalitySearchCriteria(
                    sortBy = MunicipalitySortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    fields = setOf(MunicipalityField.NAME, MunicipalityField.POPULATION),
                )

            val result = municipalityService.searchMunicipalities(criteria)
            val populations =
                result.content.mapNotNull {
                    it.getValue(MunicipalityField.POPULATION) as? Long
                }
            assertEquals(populations, populations.sortedDescending())
        }
    }
}
