package np.gov.mofaga.imis.location.service.impl

import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.mofaga.imis.location.api.dto.mapper.ProvinceMapper
import np.gov.mofaga.imis.location.api.dto.request.CreateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.response.ProvinceDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceResponse
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.exception.*
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.repository.specification.ProvinceSpecifications
import np.gov.mofaga.imis.location.service.ProvinceService
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

        return provinceMapper.toDetailResponse(province)
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
}
