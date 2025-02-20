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
        val request = DistrictTestFixtures.createDistrictRequest(provinceId = testProvince.id!!)
        val elapsed = measureTimeMillis {
            districtService.createDistrict(request)
        }
        assert(elapsed < 1000) { "District creation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark district search performance`() {
        val criteria = DistrictSearchCriteria(
            searchTerm = "Test",
            minPopulation = 50000L,
            maxPopulation = 200000L,
            sortBy = DistrictSortField.POPULATION,
            sortDirection = Sort.Direction.DESC
        )

        val elapsed = measureTimeMillis {
            districtService.searchDistricts(criteria)
        }
        assert(elapsed < 500) { "District search took more than 500ms: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark concurrent district creation performance`() {
        val requests = (1..10).map {
            DistrictTestFixtures.createDistrictRequest(
                provinceId = testProvince.id!!,
                code = "PERF-D$it"
            )
        }

        val elapsed = measureTimeMillis {
            requests.parallelStream().forEach { request ->
                try {
                    districtService.createDistrict(request)
                } catch (e: Exception) {
                    // Ignore duplicate codes in concurrent test
                }
            }
        }
        assert(elapsed < 2000) { "Concurrent district creation took more than 2 seconds: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark district statistics calculation performance`() {
        val district = districtService.createDistrict(
            DistrictTestFixtures.createDistrictRequest(provinceId = testProvince.id!!)
        )

        // Add some municipalities for statistics
        repeat(5) {
            val municipality = MunicipalityTestFixtures.createMunicipalityRequest(
                districtId = district.id,
                code = "TEST-M$it"
            )
            districtService.getMunicipalityService().createMunicipality(municipality)
        }

        val elapsed = measureTimeMillis {
            districtService.getDistrictStatistics(district.id)
        }
        assert(elapsed < 1000) { "Statistics calculation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark geospatial search performance`() {
        val elapsed = measureTimeMillis {
            districtService.findNearbyDistricts(
                latitude = BigDecimal("27.7172"),
                longitude = BigDecimal("85.3240"),
                radiusKm = 50.0,
                page = 0,
                size = 20
            )
        }
        assert(elapsed < 1000) { "Geospatial search took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark bulk update performance`() {
        val districts = districtService.searchDistricts(
            DistrictSearchCriteria(provinceId = testProvince.id)
        ).content

        val elapsed = measureTimeMillis {
            districts.forEach { district ->
                try {
                    districtService.updateDistrict(
                        district.id,
                        DistrictTestFixtures.createUpdateDistrictRequest()
                    )
                } catch (e: Exception) {
                    // Ignore concurrent modification exceptions
                }
            }
        }
        val avgTimePerUpdate = elapsed / districts.size.coerceAtLeast(1)
        assert(avgTimePerUpdate < 200) { "Average update time exceeded 200ms: $avgTimePerUpdate ms" }
    }

    @Test
    @Benchmark
    fun `benchmark district hierarchy loading performance`() {
        val district = districtService.createDistrict(
            DistrictTestFixtures.createDistrictRequest(provinceId = testProvince.id!!)
        )

        val elapsed = measureTimeMillis {
            districtService.getDistrictDetail(district.id)
        }
        assert(elapsed < 500) { "Hierarchy loading took more than 500ms: $elapsed ms" }
    }

    private fun createAndPersistProvince(): Province {
        return provinceRepository.save(ProvinceTestFixtures.createProvince())
    }

    @Transactional
    private fun createTestData() {
        // Create a substantial amount of test data for realistic performance testing
        (1..20).forEach { i ->
            try {
                districtService.createDistrict(
                    DistrictTestFixtures.createDistrictRequest(
                        provinceId = testProvince.id!!,
                        code = "PERF-D$i",
                        name = "Performance Test District $i",
                        population = (50000L + (i * 10000L))
                    )
                )
            } catch (e: Exception) {
                // Ignore duplicate codes
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options = OptionsBuilder()
                .include(DistrictPerformanceTest::class.java.simpleName)
                .build()
            Runner(options).run()
        }
    }
}
