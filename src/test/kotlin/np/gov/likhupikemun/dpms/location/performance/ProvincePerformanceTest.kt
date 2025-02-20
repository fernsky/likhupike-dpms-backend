package np.gov.likhupikemun.dpms.location.performance

import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.service.DistrictService
import np.gov.likhupikemun.dpms.location.service.MunicipalityService
import np.gov.likhupikemun.dpms.location.service.ProvinceService
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
class ProvincePerformanceTest {
    @Autowired
    private lateinit var provinceService: ProvinceService

    @Autowired
    private lateinit var districtService: DistrictService

    @Autowired
    private lateinit var municipalityService: MunicipalityService

    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    private lateinit var testProvince: Province

    @BeforeEach
    fun setup() {
        provinceRepository.deleteAll()
        createTestData()
    }

    @Test
    @Benchmark
    fun `benchmark province creation performance`() {
        val request = ProvinceTestFixtures.createProvinceRequest(code = "PERF-${System.nanoTime()}")
        val elapsed =
            measureTimeMillis {
                provinceService.createProvince(request)
            }
        assert(elapsed < 1000) { "Province creation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark province search with complex criteria performance`() {
        val criteria =
            ProvinceSearchCriteria(
                searchTerm = "Test",
                minPopulation = 100000L,
                maxPopulation = 1000000L,
                sortBy = ProvinceSortField.POPULATION,
                sortDirection = Sort.Direction.DESC,
                includeInactive = false,
                minDistricts = 2,
                minMunicipalities = 5,
            )

        val elapsed =
            measureTimeMillis {
                provinceService.searchProvinces(criteria)
            }
        assert(elapsed < 500) { "Complex search took more than 500ms: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark concurrent province creation performance`() {
        val requests =
            (1..5).map {
                ProvinceTestFixtures.createProvinceRequest(code = "CONC-$it")
            }

        val elapsed =
            measureTimeMillis {
                requests.parallelStream().forEach { request ->
                    try {
                        provinceService.createProvince(request)
                    } catch (e: Exception) {
                        // Ignore duplicate codes in concurrent test
                    }
                }
            }
        assert(elapsed < 2000) { "Concurrent province creation took more than 2 seconds: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark hierarchical data loading performance`() {
        val elapsed =
            measureTimeMillis {
                provinceService.getProvinceDetail(testProvince.id!!)
            }
        assert(elapsed < 1000) { "Hierarchical data loading took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark statistics calculation performance`() {
        val elapsed =
            measureTimeMillis {
                provinceService.getProvinceStatistics(testProvince.id!!)
            }
        assert(elapsed < 1000) { "Statistics calculation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark bulk update performance`() {
        val provinces = createBulkTestProvinces()
        val updateRequest = ProvinceTestFixtures.createUpdateProvinceRequest()

        val elapsed =
            measureTimeMillis {
                provinces.forEach { province ->
                    try {
                        provinceService.updateProvince(province.id!!, updateRequest)
                    } catch (e: Exception) {
                        // Ignore concurrent modification exceptions
                    }
                }
            }
        val avgTimePerUpdate = elapsed / provinces.size.coerceAtLeast(1)
        assert(avgTimePerUpdate < 200) { "Average update time exceeded 200ms: $avgTimePerUpdate ms" }
    }

    @Transactional
    private fun createTestData() {
        // Create main test province with complex hierarchy
        testProvince = provinceRepository.save(ProvinceTestFixtures.createProvince())

        // Create districts
        repeat(3) { districtIndex ->
            val district =
                districtService.createDistrict(
                    DistrictTestFixtures.createDistrictRequest(
                        provinceId = testProvince.id!!,
                        code = "D-$districtIndex",
                        population = 100000L + (districtIndex * 10000L),
                    ),
                )

            // Create municipalities for each district
            repeat(5) { municipalityIndex ->
                municipalityService.createMunicipality(
                    MunicipalityTestFixtures.createMunicipalityRequest(
                        districtId = district.id,
                        code = "M-$districtIndex-$municipalityIndex",
                        population = 20000L + (municipalityIndex * 1000L),
                    ),
                )
            }
        }
    }

    private fun createBulkTestProvinces(): List<Province> =
        (1..5).map { index ->
            provinceRepository.save(
                ProvinceTestFixtures.createProvince(
                    code = "BULK-$index",
                    name = "Bulk Test Province $index",
                    population = 100000L + (index * 50000L),
                ),
            )
        }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options =
                OptionsBuilder()
                    .include(ProvincePerformanceTest::class.java.simpleName)
                    .build()
            Runner(options).run()
        }
    }
}
