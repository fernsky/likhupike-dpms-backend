package np.gov.likhupikemun.dpms.location.service.impl

import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.mapper.ProvinceMapper
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateProvinceRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateProvinceRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceStats
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import np.gov.likhupikemun.dpms.location.repository.specification.ProvinceSpecifications
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProvinceServiceImpl(
    private val provinceRepository: ProvinceRepository,
    private val provinceMapper: ProvinceMapper,
) : ProvinceService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun createProvince(request: CreateProvinceRequest): ProvinceResponse {
        logger.info("Creating province with code: ${request.code}")

        validateProvinceCode(request.code)

        val province =
            Province().apply {
                name = request.name
                nameNepali = request.nameNepali
                code = request.code.uppercase()
                area = request.area
                population = request.population
                headquarter = request.headquarter
                headquarterNepali = request.headquarterNepali
            }

        return provinceRepository
            .save(province)
            .let { provinceMapper.toResponse(it) }
            .also { logger.info("Created province with code: ${it.code}") }
    }

    override fun updateProvince(
        code: String,
        request: UpdateProvinceRequest,
    ): ProvinceResponse {
        logger.info("Updating province: $code")

        val province = getProvinceEntity(code)

        province.apply {
            request.name?.let { name = it }
            request.nameNepali?.let { nameNepali = it }
            request.area?.let { area = it }
            request.population?.let { population = it }
            request.headquarter?.let { headquarter = it }
            request.headquarterNepali?.let { headquarterNepali = it }
        }

        return provinceRepository
            .save(province)
            .let { provinceMapper.toResponse(it) }
            .also { logger.info("Updated province: $code") }
    }

    override fun getProvinceDetail(code: String): ProvinceDetailResponse {
        logger.debug("Fetching detailed information for province: $code")

        val province = getProvinceEntity(code)
        val stats = getProvinceStatistics(code)

        return provinceMapper.toDetailResponse(province, stats)
    }

    override fun getProvince(code: String): ProvinceResponse {
        logger.debug("Fetching province: $code")
        return getProvinceEntity(code).let { provinceMapper.toResponse(it) }
    }

    override fun searchProvinces(criteria: ProvinceSearchCriteria): Page<ProvinceResponse> {
        logger.debug("Searching provinces with criteria: $criteria")

        val specification = ProvinceSpecifications.withSearchCriteria(criteria)
        val pageable =
            PageRequest.of(
                criteria.page,
                criteria.pageSize,
                Sort.by(criteria.sortDirection, criteria.sortBy.toEntityField()),
            )

        return provinceRepository
            .findAll(specification, pageable)
            .map { provinceMapper.toResponse(it) }
    }

    override fun getProvinceStatistics(code: String): ProvinceStats {
        logger.debug("Calculating statistics for province: $code")

        val province = getProvinceEntity(code)
        return calculateProvinceStats(province)
    }

    override fun getAllProvinces(): List<ProvinceResponse> {
        logger.debug("Fetching all provinces")
        return provinceRepository
            .findAll()
            .map { provinceMapper.toResponse(it) }
    }

    override fun findLargeProvinces(
        minArea: BigDecimal,
        minPopulation: Long,
        page: Int,
        size: Int,
    ): Page<ProvinceResponse> {
        logger.debug("Finding large provinces with minArea: $minArea, minPopulation: $minPopulation")

        return provinceRepository
            .findLargeProvinces(minArea, minPopulation, PageRequest.of(page, size))
            .map { provinceMapper.toResponse(it) }
    }

    override fun validateProvinceExists(code: String) {
        if (!provinceRepository.existsByCode(code.uppercase())) {
            throw ProvinceNotFoundException(code)
        }
    }

    private fun getProvinceEntity(code: String): Province =
        provinceRepository
            .findByCodeIgnoreCase(code)
            .orElseThrow { ProvinceNotFoundException(code) }

    private fun validateProvinceCode(code: String) {
        if (provinceRepository.existsByCode(code.uppercase())) {
            throw ProvinceCodeExistsException(code)
        }
    }

    private fun calculateProvinceStats(province: Province): ProvinceStats {
        val districts = province.districts
        val municipalities = districts.flatMap { it.municipalities }

        return ProvinceStats(
            totalDistricts = districts.size,
            totalMunicipalities = municipalities.size,
            totalPopulation = municipalities.sumOf { it.population ?: 0L },
            totalArea =
                municipalities
                    .mapNotNull { it.area }
                    .fold(BigDecimal.ZERO) { acc, area -> acc.add(area) },
            populationDensity = calculatePopulationDensity(province),
            municipalityTypes = calculateMunicipalityTypeBreakdown(municipalities),
        )
    }

    private fun calculatePopulationDensity(province: Province): BigDecimal {
        val area = province.area ?: return BigDecimal.ZERO
        if (area == BigDecimal.ZERO) return BigDecimal.ZERO

        val population = province.population ?: 0L
        return BigDecimal(population).divide(area, 2, BigDecimal.ROUND_HALF_UP)
    }

    private fun calculateMunicipalityTypeBreakdown(municipalities: List<Municipality>): Map<String, Int> =
        municipalities
            .groupBy { it.type }
            .mapKeys { it.key.toString() }
            .mapValues { it.value.size }
}
