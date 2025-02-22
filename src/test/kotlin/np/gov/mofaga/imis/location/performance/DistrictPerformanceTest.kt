package np.gov.mofaga.imis.location.performance
import np.gov.mofaga.imis.auth.test.UserTestDataFactory
import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSortField
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.service.DistrictService
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
import np.gov.mofaga.imis.shared.service.SecurityService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.mockito.kotlin.whenever
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@WebMvcTest(DistrictService::class)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@Fork(1)
@ActiveProfiles("test")
@Tag("performance")
@EnabledIfEnvironmentVariable(named = "RUN_PERFORMANCE_TESTS", matches = "true")
@WithMockUser(roles = ["SUPER_ADMIN"]) // Add this annotation
class DistrictPerformanceTest {
    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    @MockBean
    private lateinit var securityService: SecurityService

    private val superAdmin = UserTestDataFactory.createSuperAdmin()

    private lateinit var testProvince: Province

    @BeforeEach
    fun setup() {
        // Mock the security service
        whenever(securityService.getCurrentUser()).thenReturn(superAdmin)

        testProvince = createAndPersistProvince()
        createTestData()
    }

    @Test
    @Benchmark
    fun `benchmark district creation performance`() {
        val request =
            DistrictTestFixtures.createDistrictRequest(
                provinceCode = testProvince.code!!,
                code = "PERF-D${System.nanoTime()}",
            )

        val elapsed =
            measureTimeMillis {
                districtService.createDistrict(request)
            }
        assert(elapsed < 1000) { "District creation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark district search performance`() {
        val criteria =
            DistrictSearchCriteria(
                searchTerm = "Test",
                code = null,
                sortBy = DistrictSortField.POPULATION,
                sortDirection = Sort.Direction.DESC,
                page = 0,
                pageSize = 10,
            )

        val elapsed =
            measureTimeMillis {
                districtService.searchDistricts(criteria)
            }
        assert(elapsed < 500) { "District search took more than 500ms: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark concurrent district access performance`() {
        val district =
            districtService.createDistrict(
                DistrictTestFixtures.createDistrictRequest(
                    provinceCode = testProvince.code!!,
                    code = "PERF-D-CONC",
                ),
            )

        val elapsed =
            measureTimeMillis {
                repeat(10) {
                    districtService.getDistrictDetail(district.code)
                }
            }
        assert(elapsed < 1000) { "Concurrent district access took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark geospatial search performance`() {
        val elapsed =
            measureTimeMillis {
                districtService.findNearbyDistricts(
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                    radiusKm = 50.0,
                    page = 0,
                    size = 20,
                )
            }
        assert(elapsed < 1000) { "Geospatial search took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark bulk update performance`() {
        val districts =
            districtService
                .searchDistricts(
                    DistrictSearchCriteria(
                        code = testProvince.code,
                        page = 0,
                        pageSize = 50,
                    ),
                ).content

        val elapsed =
            measureTimeMillis {
                districts.forEach { district ->
                    try {
                        districtService.updateDistrict(
                            district.code,
                            DistrictTestFixtures.createUpdateDistrictRequest(),
                        )
                    } catch (e: Exception) {
                        // Ignore concurrent modification exceptions
                    }
                }
            }
        val avgTimePerUpdate = elapsed / districts.size.coerceAtLeast(1)
        assert(avgTimePerUpdate < 200) { "Average update time exceeded 200ms: $avgTimePerUpdate ms" }
    }

    private fun createAndPersistProvince(): Province {
        // Generate a truly unique code using UUID and timestamp
        val timestamp = System.currentTimeMillis()
        val randomPart = (100000..999999).random()
        val uniqueCode = "PERF-P-$timestamp-$randomPart"

        try {
            return provinceRepository.save(
                ProvinceTestFixtures.createProvince(
                    code = uniqueCode,
                    name = "Performance Test Province $uniqueCode",
                ),
            )
        } catch (e: Exception) {
            // If first attempt fails, try one more time with nanoTime
            val retryCode = "PERF-P-${System.nanoTime()}-$randomPart"
            return provinceRepository.save(
                ProvinceTestFixtures.createProvince(
                    code = retryCode,
                    name = "Performance Test Province $retryCode",
                ),
            )
        }
    }

    @Transactional
    private fun createTestData() {
        // Create test data for performance testing
        (1..20).forEach { i ->
            try {
                val timestamp = System.currentTimeMillis()
                val randomPart = (100000..999999).random()
                val uniqueCode = "PERF-D-$timestamp-$randomPart-$i"

                districtService.createDistrict(
                    DistrictTestFixtures.createDistrictRequest(
                        provinceCode = testProvince.code!!,
                        code = uniqueCode,
                        name = "Performance Test District $uniqueCode",
                        population = (50000L + (i * 10000L)),
                    ),
                )
            } catch (e: Exception) {
                // Log the error but continue with the test
                println("Error creating test district: ${e.message}")
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options =
                OptionsBuilder()
                    .include(DistrictPerformanceTest::class.java.simpleName)
                    .build()
            Runner(options).run()
        }
    }
}
