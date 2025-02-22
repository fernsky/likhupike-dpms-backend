package np.gov.mofaga.imis.location.service.impl

import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.mofaga.imis.location.api.dto.mapper.DistrictMapper
import np.gov.mofaga.imis.location.api.dto.request.CreateDistrictRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateDistrictRequest
import np.gov.mofaga.imis.location.api.dto.response.DistrictDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.DistrictResponse
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.exception.*
import np.gov.mofaga.imis.location.repository.DistrictRepository
import np.gov.mofaga.imis.location.repository.ProvinceRepository
import np.gov.mofaga.imis.location.repository.specification.DistrictSpecifications
import np.gov.mofaga.imis.location.service.DistrictService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class DistrictServiceImpl(
    private val districtRepository: DistrictRepository,
    private val provinceRepository: ProvinceRepository,
    private val districtMapper: DistrictMapper,
) : DistrictService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun createDistrict(request: CreateDistrictRequest): DistrictResponse {
        logger.info("Creating district with code: ${request.code}")

        val province =
            provinceRepository
                .findByCodeIgnoreCase(request.provinceCode)
                .orElseThrow { ProvinceNotFoundException(request.provinceCode) }

        validateDistrictCode(request.code, request.provinceCode)

        val district =
            District().apply {
                name = request.name
                nameNepali = request.nameNepali
                code = request.code.uppercase()
                area = request.area
                population = request.population
                headquarter = request.headquarter
                headquarterNepali = request.headquarterNepali
                this.province = province
            }

        return districtRepository
            .save(district)
            .let { districtMapper.toResponse(it) }
            .also { logger.info("Created district with code: ${it.code}") }
    }

    override fun updateDistrict(
        code: String,
        request: UpdateDistrictRequest,
    ): DistrictResponse {
        logger.info("Updating district: $code")

        val district = getDistrictEntity(code)

        district.apply {
            request.name?.let { name = it }
            request.nameNepali?.let { nameNepali = it }
            request.area?.let { area = it }
            request.population?.let { population = it }
            request.headquarter?.let { headquarter = it }
            request.headquarterNepali?.let { headquarterNepali = it }
        }

        return districtRepository
            .save(district)
            .let { districtMapper.toResponse(it) }
            .also { logger.info("Updated district: $code") }
    }

    @Transactional(readOnly = true)
    override fun getDistrictDetail(code: String): DistrictDetailResponse {
        logger.debug("Fetching detailed information for district: $code")

        val district = getDistrictEntity(code)

        return districtMapper.toDetailResponse(district)
    }

    @Transactional(readOnly = true)
    override fun searchDistricts(criteria: DistrictSearchCriteria): Page<DistrictResponse> {
        logger.debug("Searching districts with criteria: $criteria")

        criteria.validate()

        val specification = DistrictSpecifications.withSearchCriteria(criteria)
        val pageable = buildPageRequest(criteria)

        return districtRepository
            .findAll(specification, pageable)
            .map { districtMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun getDistrict(code: String): DistrictResponse {
        logger.debug("Fetching district: $code")
        return getDistrictEntity(code)
            .let { districtMapper.toResponse(it) }
    }

    override fun getAllDistricts(): List<DistrictResponse> {
        logger.debug("Fetching all districts")
        return districtRepository
            .findAll()
            .map { districtMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun getDistrictsByProvince(provinceCode: String): List<DistrictResponse> {
        logger.debug("Fetching districts for province: $provinceCode")
        return districtRepository
            .findByProvinceCode(provinceCode)
            .map { districtMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun findNearbyDistricts(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): Page<DistrictResponse> {
        logger.debug("Finding districts within ${radiusKm}km of ($latitude, $longitude)")

        validateCoordinates(latitude, longitude)
        validateRadius(radiusKm)

        val radiusInMeters = radiusKm * 1000
        val pageable = PageRequest.of(page, size)

        return districtRepository
            .findNearbyDistricts(latitude, longitude, radiusInMeters, pageable)
            .map { districtMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun validateDistrictExists(code: String) {
        if (!districtRepository.existsByCode(code.uppercase())) {
            throw DistrictNotFoundException(code)
        }
    }

    @Transactional(readOnly = true)
    override fun findLargeDistricts(
        minArea: BigDecimal,
        minPopulation: Long,
        page: Int,
        size: Int,
    ): Page<DistrictResponse> {
        logger.debug("Finding large districts with minArea: $minArea, minPopulation: $minPopulation")
        val pageable = PageRequest.of(page, size)
        return districtRepository
            .findLargeDistricts(minPopulation, minArea, pageable)
            .map { districtMapper.toResponse(it) }
    }

    // Private helper methods
    private fun getDistrictEntity(code: String): District =
        districtRepository
            .findByCodeIgnoreCase(code)
            .orElseThrow { DistrictNotFoundException(code) }

    private fun validateDistrictCode(
        code: String,
        provinceCode: String,
    ) {
        if (districtRepository.existsByCodeAndProvince(code.uppercase(), provinceCode.uppercase())) {
            throw DistrictCodeExistsException(code)
        }
    }

    private fun buildPageRequest(criteria: DistrictSearchCriteria): PageRequest =
        PageRequest.of(
            criteria.page,
            criteria.pageSize,
            Sort.by(criteria.sortDirection, criteria.sortBy.toEntityField()),
        )

    private fun validateCoordinates(
        latitude: BigDecimal,
        longitude: BigDecimal,
    ) {
        require(latitude.abs() <= BigDecimal("90")) { "Invalid latitude: $latitude" }
        require(longitude.abs() <= BigDecimal("180")) { "Invalid longitude: $longitude" }
    }

    private fun validateRadius(radiusKm: Double) {
        require(radiusKm > 0) { "Radius must be positive" }
        require(radiusKm <= MAX_SEARCH_RADIUS_KM) { "Radius exceeds maximum allowed value of $MAX_SEARCH_RADIUS_KM km" }
    }

    companion object {
        private const val MAX_SEARCH_RADIUS_KM = 100.0
    }
}
