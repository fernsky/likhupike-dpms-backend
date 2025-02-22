package np.gov.mofaga.imis.location.performance

import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSortField
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.service.DistrictService
import np.gov.mofaga.imis.location.service.MunicipalityService
import np.gov.mofaga.imis.location.service.ProvinceService
import np.gov.mofaga.imis.location.test.fixtures.DistrictTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.MunicipalityTestFixtures
import np.gov.mofaga.imis.location.test.fixtures.ProvinceTestFixtures
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
        testProvince = createTestData()
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
    fun `benchmark province search performance`() {
        val criteria =
            ProvinceSearchCriteria(
                searchTerm = "Test",
                code = null,
                sortBy = ProvinceSortField.POPULATION,
                sortDirection = Sort.Direction.DESC,
                page = 0,
                pageSize = 10,
            )

        val elapsed =
            measureTimeMillis {
                provinceService.searchProvinces(criteria)
            }
        assert(elapsed < 500) { "Search took more than 500ms: $elapsed ms" }
    }

    @Test
    @Benchmark
    fun `benchmark province detail retrieval performance`() {
        val elapsed =
            measureTimeMillis {
                provinceService.getProvinceDetail(testProvince.code!!)
            }
        assert(elapsed < 500) { "Detail retrieval took more than 500ms: $elapsed ms" }
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
                        provinceService.updateProvince(province.code!!, updateRequest)
                    } catch (e: Exception) {
                        // Ignore concurrent modification exceptions
                    }
                }
            }
        val avgTimePerUpdate = elapsed / provinces.size.coerceAtLeast(1)
        assert(avgTimePerUpdate < 200) { "Average update time exceeded 200ms: $avgTimePerUpdate ms" }
    }

    @Transactional
    private fun createTestData(): Province {
        // Create main test province with hierarchy
        val province = provinceRepository.save(ProvinceTestFixtures.createProvince())

        // Create districts
        repeat(3) { districtIndex ->
            val district =
                districtService.createDistrict(
                    DistrictTestFixtures.createDistrictRequest(
                        provinceCode = province.code!!,
                        code = "D-$districtIndex",
                        population = 100000L + (districtIndex * 10000L),
                    ),
                )

            // Create municipalities for each district
            repeat(5) { municipalityIndex ->
                municipalityService.createMunicipality(
                    MunicipalityTestFixtures.createMunicipalityRequest(
                        districtCode = district.code,
                        code = "M-$districtIndex-$municipalityIndex",
                        population = 20000L + (municipalityIndex * 1000L),
                    ),
                )
            }
        }
        return province
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
