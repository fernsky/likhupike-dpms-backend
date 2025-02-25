package np.gov.mofaga.imis.location.service

import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.location.api.dto.criteria.WardSearchCriteria
import np.gov.mofaga.imis.location.api.dto.criteria.WardSortField
import np.gov.mofaga.imis.location.api.dto.enums.WardField
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.repository.DistrictRepository
import np.gov.mofaga.imis.location.repository.MunicipalityRepository
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.repository.WardRepository
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.WardTestFixtures
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

@WebMvcTest(WardService::class)
@Import(TestSecurityConfig::class)
@ActiveProfiles("test")
@DisplayName("Ward Search Integration Tests")
@WithMockUser(roles = ["SUPER_ADMIN"])
class WardSearchIntegrationTest {
    @Autowired
    private lateinit var wardService: WardService

    @Autowired
    private lateinit var wardRepository: WardRepository

    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    private lateinit var testProvince: Province
    private lateinit var testDistrict: District
    private lateinit var testMunicipality: Municipality

    @BeforeEach
    fun setup() {
        wardRepository.deleteAll()
        municipalityRepository.deleteAll()
        districtRepository.deleteAll()
        provinceRepository.deleteAll()
        setupTestData()
    }

    private fun setupTestData() {
        testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())
        testDistrict = districtRepository.save(DistrictTestFixtures.createDistrict(province = testProvince))
        testMunicipality = municipalityRepository.save(MunicipalityTestFixtures.createMunicipality(district = testDistrict))

        WardTestFixtures.createSearchTestData().forEach { ward ->
            ward.municipality = testMunicipality
            wardRepository.save(ward)
        }
    }

    @Nested
    @DisplayName("Dynamic Field Selection Tests")
    inner class DynamicFieldSelectionTests {
        @Test
        @Transactional
        fun `should honor field selection in search results`() {
            val criteria =
                WardSearchCriteria(
                    fields = setOf(WardField.WARD_NUMBER, WardField.POPULATION),
                    municipalityCode = "TEST-M",
                )

            val result = wardService.searchWards(criteria)
            val ward = result.content.first()

            assertNotNull(ward.getValue(WardField.WARD_NUMBER))
            assertNotNull(ward.getValue(WardField.POPULATION))
            assertNull(ward.getValue(WardField.AREA))
        }
    }

    @Nested
    @DisplayName("Advanced Search Tests")
    inner class AdvancedSearchTests {
        @Test
        @Transactional
        fun `should search with geometry included`() {
            val criteria =
                WardSearchCriteria(
                    fields = setOf(WardField.WARD_NUMBER, WardField.GEOMETRY),
                    includeGeometry = true,
                )

            val result = wardService.searchWards(criteria)
            val ward = result.content.first()

            assertNotNull(ward.getValue(WardField.GEOMETRY))
        }

        @Test
        @Transactional
        fun `should sort by population in descending order`() {
            val criteria =
                WardSearchCriteria(
                    sortBy = WardSortField.POPULATION,
                    sortDirection = Sort.Direction.DESC,
                    fields = setOf(WardField.WARD_NUMBER, WardField.POPULATION),
                )

            val result = wardService.searchWards(criteria)
            val populations =
                result.content.mapNotNull {
                    it.getValue(WardField.POPULATION) as? Long
                }
            assertEquals(populations, populations.sortedDescending())
        }
    }
}
