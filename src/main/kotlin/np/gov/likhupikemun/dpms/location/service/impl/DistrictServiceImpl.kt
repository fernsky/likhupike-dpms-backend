package np.gov.likhupikemun.dpms.location.service.impl

import np.gov.likhupikemun.dpms.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.mapper.DistrictMapper
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictStats
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.repository.ProvinceRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
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
    override fun getDistrictDetail(id: UUID): DistrictDetailResponse {
        logger.debug("Fetching detailed information for district: $id")

        val district = getDistrictEntity(id)
        val stats = getDistrictStatistics(id)

        return districtMapper.toDetailResponse(district, stats)
    }

    @Transactional(readOnly = true)
    override fun searchDistricts(criteria: DistrictSearchCriteria): Page<DistrictResponse> {
        logger.debug("Searching districts with criteria: $criteria")

        criteria.validate()

        val specification = DistrictSpecifications.withSearchCriteria(criteria)
        val pageable =
            PageRequest.of(
                criteria.page,
                criteria.pageSize,
                Sort.by(criteria.sortDirection, criteria.sortBy.toEntityField()),
            )

        return districtRepository
            .findAll(specification, pageable)
            .map { districtMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun getDistrict(id: UUID): DistrictResponse {
        logger.debug("Fetching district: $id")
        return getDistrictEntity(id)
            .let { districtMapper.toResponse(it) }
    }

    override fun getAllDistricts(): List<DistrictResponse> {
        logger.debug("Fetching all districts")
        return districtRepository
            .findAll()
            .map { districtMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun getDistrictsByProvince(provinceId: UUID): List<DistrictResponse> {
        logger.debug("Fetching districts for province: $provinceId")

        if (!provinceRepository.existsById(provinceId)) {
            throw ProvinceNotFoundException(provinceId)
        }

        return districtRepository
            .findByProvinceIdAndIsActive(provinceId, true)
            .map { districtMapper.toResponse(it) }
    }

    override fun getDistrictsByProvince(provinceCode: String): List<DistrictResponse> {
        logger.debug("Fetching districts for province: $provinceCode")
        return districtRepository
            .findByProvinceCode(provinceCode)
            .map { districtMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun getDistrictStatistics(id: UUID): DistrictStats {
        logger.debug("Fetching statistics for district: $id")

        return districtRepository
            .getDistrictStatistics(id)
            .orElseThrow { DistrictNotFoundException(id) }
    }

    @Transactional
    override fun deactivateDistrict(id: UUID) {
        logger.info("Deactivating district: $id")

        val district = getDistrictEntity(id)

        if (district.municipalities.any { it.isActive }) {
            throw DistrictOperationException.hasActiveMunicipalities(id)
        }

        district.isActive = false
        districtRepository.save(district)
        logger.info("District $id deactivated successfully")
    }

    @Transactional
    override fun reactivateDistrict(id: UUID) {
        logger.info("Reactivating district: $id")

        val district = getDistrictEntity(id)

        if (district.isActive) {
            throw DistrictOperationException(
                message = "District is already active",
                errorCode = "DISTRICT_ALREADY_ACTIVE",
            )
        }

        // Check if province is active
        if (!district.province?.isActive!!) {
            throw DistrictOperationException(
                message = "Cannot reactivate district in inactive province",
                errorCode = "PROVINCE_INACTIVE",
            )
        }

        district.isActive = true
        districtRepository.save(district)
        logger.info("District $id reactivated successfully")
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
    override fun getDistrictAnalytics(id: UUID): DistrictStats {
        logger.debug("Generating analytics for district: $id")

        val district = getDistrictEntity(id)
        val baseStats = getDistrictStatistics(id)

        // Enhance base stats with additional analytics
        return baseStats.copy(
            demographicBreakdown = calculateDemographicBreakdown(district),
            economicIndicators = calculateEconomicIndicators(district),
            infrastructureMetrics = calculateInfrastructureMetrics(district),
        )
    }

    @Transactional(readOnly = true)
    override fun validateDistrictExists(districtId: UUID) {
        if (!districtRepository.existsById(districtId)) {
            throw DistrictNotFoundException(districtId)
        }
    }

    override fun validateDistrictExists(code: String) {
        if (!districtRepository.existsByCode(code.uppercase())) {
            throw DistrictNotFoundException(code)
        }
    }

    // Private helper methods
    private fun getDistrictEntity(id: UUID): District =
        districtRepository
            .findById(id)
            .orElseThrow { DistrictNotFoundException(id) }

    private fun getDistrictEntity(code: String): District =
        districtRepository
            .findByCodeIgnoreCase(code)
            .orElseThrow { DistrictNotFoundException(code) }

    private fun validateDistrictCode(
        code: String,
        provinceId: UUID,
    ) {
        if (districtRepository.existsByCodeAndProvince(code, provinceId, null)) {
            throw DuplicateDistrictCodeException(code, provinceId)
        }
    }

    private fun validateDistrictCode(
        code: String,
        provinceCode: String,
    ) {
        if (districtRepository.existsByCodeAndProvince(code.uppercase(), provinceCode)) {
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

    private fun calculateDemographicBreakdown(district: District): Map<String, Long> {
        // Implementation for demographic breakdown calculation
        return district.municipalities
            .filter { it.isActive }
            .flatMap { it.wards }
            .filter { it.isActive }
            .groupBy { "Category" } // Replace with actual demographic categories
            .mapValues { it.value.sumOf { ward -> ward.population ?: 0L } }
    }

    private fun calculateEconomicIndicators(district: District): EconomicIndicators {
        // Implementation for economic indicators calculation
        return EconomicIndicators(
            totalBusinesses = 0, // Implement actual calculation
            averageIncome = BigDecimal.ZERO, // Implement actual calculation
            employmentRate = BigDecimal.ZERO, // Implement actual calculation
        )
    }

    private fun calculateInfrastructureMetrics(district: District): InfrastructureMetrics {
        // Implementation for infrastructure metrics calculation
        return InfrastructureMetrics(
            roadCoverageKm = BigDecimal.ZERO, // Implement actual calculation
            waterAccessPercent = BigDecimal.ZERO, // Implement actual calculation
            electricityAccessPercent = BigDecimal.ZERO, // Implement actual calculation
        )
    }

    companion object {
        private const val MAX_SEARCH_RADIUS_KM = 100.0
    }
}

// Additional data classes for analytics
private data class EconomicIndicators(
    val totalBusinesses: Long,
    val averageIncome: BigDecimal,
    val employmentRate: BigDecimal,
)

private data class InfrastructureMetrics(
    val roadCoverageKm: BigDecimal,
    val waterAccessPercent: BigDecimal,
    val electricityAccessPercent: BigDecimal,
)
