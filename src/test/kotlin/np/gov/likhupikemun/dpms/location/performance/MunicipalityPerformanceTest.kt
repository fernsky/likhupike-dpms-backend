package np.gov.likhupikemun.dpms.location.performance

import np.gov.likhupikemun.dpms.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.service.MunicipalityService
import np.gov.likhupikemun.dpms.location.test.fixtures.DistrictTestFixtures
import np.gov.likhupikemun.dpms.location.test.fixtures.MunicipalityTestFixtures
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
class MunicipalityPerformanceTest {
    @Autowired
    private lateinit var municipalityService: MunicipalityService

    @Autowired
    private lateinit var districtRepository: DistrictRepository

    private lateinit var testDistrict: District

    @BeforeEach
    fun setup() {
        testDistrict = createAndPersistDistrict()
        createTestData()
    }

    @Test
    @Benchmark
    fun `benchmark municipality creation performance`() {
        val request = MunicipalityTestFixtures.createMunicipalityRequest(districtId = testDistrict.id!!)
        val elapsed =
            measureTimeMillis {
                municipalityService.createMunicipality(request)
            }
        assert(elapsed < 1000) { "Municipality creation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark municipality search performance`() {
        val criteria =
            MunicipalitySearchCriteria(
                searchTerm = "Test",
                types = setOf(MunicipalityType.MUNICIPALITY),
                minPopulation = 10000L,
                maxPopulation = 100000L,
                sortBy = MunicipalitySortField.POPULATION,
                sortDirection = Sort.Direction.DESC,
            )

        val elapsed =
            measureTimeMillis {
                municipalityService.searchMunicipalities(criteria)
            }
        assert(elapsed < 500) { "Municipality search took more than 500ms: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark concurrent municipality creation performance`() {
        val requests =
            (1..10).map {
                MunicipalityTestFixtures.createMunicipalityRequest(
                    districtId = testDistrict.id!!,
                    code = "TEST-M$it",
                )
            }

        val elapsed =
            measureTimeMillis {
                requests.parallelStream().forEach { request ->
                    try {
                        municipalityService.createMunicipality(request)
                    } catch (e: Exception) {
                        // Ignore duplicate codes in concurrent test
                    }
                }
            }
        assert(elapsed < 2000) { "Concurrent municipality creation took more than 2 seconds: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark municipality statistics calculation performance`() {
        val municipality =
            municipalityService.createMunicipality(
                MunicipalityTestFixtures.createMunicipalityRequest(districtId = testDistrict.id!!),
            )

        val elapsed =
            measureTimeMillis {
                municipalityService.getMunicipalityStatistics(municipality.id)
            }
        assert(elapsed < 1000) { "Statistics calculation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark geospatial search performance`() {
        val elapsed =
            measureTimeMillis {
                municipalityService.findNearbyMunicipalities(
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                    radiusKm = 10.0,
                    page = 0,
                    size = 20,
                )
            }
        assert(elapsed < 1000) { "Geospatial search took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark bulk update performance`() {
        val municipalities =
            municipalityService
                .searchMunicipalities(
                    MunicipalitySearchCriteria(districtId = testDistrict.id),
                ).content

        val elapsed =
            measureTimeMillis {
                municipalities.forEach { municipality ->
                    try {
                        municipalityService.updateMunicipality(
                            municipality.id,
                            MunicipalityTestFixtures.createUpdateMunicipalityRequest(),
                        )
                    } catch (e: Exception) {
                        // Ignore concurrent modification exceptions
                    }
                }
            }
        val avgTimePerUpdate = elapsed / municipalities.size.coerceAtLeast(1)
        assert(avgTimePerUpdate < 200) { "Average update time exceeded 200ms: $avgTimePerUpdate ms" }
    }

    private fun createAndPersistDistrict(): District = districtRepository.save(DistrictTestFixtures.createDistrict())

    @Transactional
    private fun createTestData() {
        // Create a substantial amount of test data for realistic performance testing
        (1..20).forEach { i ->
            try {
                municipalityService.createMunicipality(
                    MunicipalityTestFixtures.createMunicipalityRequest(
                        districtId = testDistrict.id!!,
                        code = "PERF-M$i",
                        name = "Performance Test Municipality $i",
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
                    .include(MunicipalityPerformanceTest::class.java.simpleName)
                    .build()
            Runner(options).run()
        }
    }
}
