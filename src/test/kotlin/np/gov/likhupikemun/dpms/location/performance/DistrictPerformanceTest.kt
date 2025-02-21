package np.gov.likhupikemun.dpms.location.performance

import np.gov.likhupikemun.dpms.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.service.DistrictService
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.ProvinceTestFixtures
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@Fork(1)
@SpringBootTest
@ActiveProfiles("test")
@Tag("performance")
@EnabledIfEnvironmentVariable(named = "RUN_PERFORMANCE_TESTS", matches = "true")
class DistrictPerformanceTest {
    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    private lateinit var testProvince: Province

    @BeforeEach
    fun setup() {
        testProvince = createAndPersistProvince()
        createTestData()
    }

    @Test
    @Benchmark
    fun `benchmark district creation performance`() {
        val request =
            DistrictTestFixtures.createDistrictRequest(
                provinceId = testProvince.id!!,
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
                minPopulation = 50000L,
                maxPopulation = 200000L,
                sortBy = "population",
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
                    provinceId = testProvince.id!!,
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
    fun `benchmark district statistics calculation performance`() {
        val district =
            districtService.createDistrict(
                DistrictTestFixtures.createDistrictRequest(
                    provinceId = testProvince.id!!,
                    code = "PERF-D-STAT",
                ),
            )

        // Add municipalities for statistics
        repeat(5) {
            val municipality =
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtCode = district.code,
                    code = "PERF-M$it",
                )
            districtService.getMunicipalityService().createMunicipality(municipality)
        }

        val elapsed =
            measureTimeMillis {
                districtService.getDistrictStatistics(district.code)
            }
        assert(elapsed < 1000) { "Statistics calculation took more than 1 second: $elapsed ms" }
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
                        provinceCode = testProvince.code,
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

    private fun createAndPersistProvince(): Province = provinceRepository.save(ProvinceTestFixtures.createProvince())

    @Transactional
    private fun createTestData() {
        // Create test data for performance testing
        (1..20).forEach { i ->
            try {
                districtService.createDistrict(
                    DistrictTestFixtures.createDistrictRequest(
                        provinceId = testProvince.id!!,
                        code = "PERF-D$i",
                        name = "Performance Test District $i",
                        population = (50000L + (i * 10000L)),
                    ),
                )
            } catch (e: Exception) {
                // Ignore duplicate codes
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
