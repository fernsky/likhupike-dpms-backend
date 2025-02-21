package np.gov.likhupikemun.dpms.location.performance

import np.gov.likhupikemun.dpms.location.api.dto.criteria.WardSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.location.service.WardService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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
class WardPerformanceTest {
    @Autowired
    private lateinit var wardService: WardService

    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    private lateinit var testMunicipality: Municipality

    @BeforeEach
    fun setup() {
        testMunicipality = createAndPersistMunicipality()
        createTestData()
    }

    @Test
    @Benchmark
    fun `benchmark ward creation performance`() {
        val request = createWardRequest()
        val elapsed =
            measureTimeMillis {
                wardService.createWard(request)
            }
        assert(elapsed < 1000) { "Ward creation took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark ward search performance`() {
        val criteria =
            WardSearchCriteria(
                municipalityCode = testMunicipality.code,
                minPopulation = 1000L,
                maxPopulation = 5000L,
                page = 0,
                pageSize = 20,
            )

        val elapsed =
            measureTimeMillis {
                wardService.searchWards(criteria)
            }
        assert(elapsed < 500) { "Ward search took more than 500ms: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark concurrent ward creation performance`() {
        val requests = (1..10).map { createWardRequest(it) }
        val elapsed =
            measureTimeMillis {
                requests.parallelStream().forEach { request ->
                    try {
                        wardService.createWard(request)
                    } catch (e: Exception) {
                        // Ignore duplicate ward numbers
                    }
                }
            }
        assert(elapsed < 2000) { "Concurrent ward creation took more than 2 seconds: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark ward detail retrieval performance`() {
        val ward = wardService.createWard(createWardRequest())
        val elapsed =
            measureTimeMillis {
                wardService.getWardDetail(ward.wardNumber, testMunicipality.code!!)
            }
        assert(elapsed < 1000) { "Ward detail retrieval took more than 1 second: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark geospatial search performance`() {
        val elapsed =
            measureTimeMillis {
                wardService.findNearbyWards(
                    latitude = BigDecimal("27.7172"),
                    longitude = BigDecimal("85.3240"),
                    radiusKm = 5.0,
                    page = 0,
                    size = 20,
                )
            }
        assert(elapsed < 1000) { "Geospatial search took more than 1 second: $elapsed ms" }
    }

    private fun createAndPersistMunicipality(): Municipality {
        val municipality =
            Municipality().apply {
                name = "Performance Test Municipality"
                nameNepali = "कार्यसम्पादन परीक्षण नगरपालिका"
                code = "PERF-01"
                type = MunicipalityType.MUNICIPALITY
                area = BigDecimal("100.00")
                population = 10000L
                totalWards = 33
            }
        return municipalityRepository.save(municipality)
    }

    private fun createWardRequest(number: Int = 1) =
        CreateWardRequest(
            municipalityCode = testMunicipality.code!!, // Add null-safe operator
            wardNumber = number, // Remove unnecessary null-safe operator
            area = BigDecimal("10.00"),
            population = 1000L,
            latitude = BigDecimal("27.7172"),
            longitude = BigDecimal("85.3240"),
            officeLocation = "Test Office $number",
            officeLocationNepali = "परीक्षण कार्यालय $number",
        )

    @Transactional
    private fun createTestData() {
        // Create a substantial amount of test data for realistic performance testing
        (1..20).forEach { i ->
            try {
                wardService.createWard(createWardRequest(i))
            } catch (e: Exception) {
                // Ignore duplicate ward numbers
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options =
                OptionsBuilder()
                    .include(WardPerformanceTest::class.java.simpleName)
                    .build()
            Runner(options).run()
        }
    }
}
