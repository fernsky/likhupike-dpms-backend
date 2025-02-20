package np.gov.likhupikemun.dpms.location.service.impl

import np.gov.likhupikemun.dpms.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.mapper.MunicipalityMapper
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityStats
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.DistrictRepository
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.location.repository.specification.MunicipalitySpecifications
import np.gov.likhupikemun.dpms.location.service.MunicipalityService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class MunicipalityServiceImpl(
    private val municipalityRepository: MunicipalityRepository,
    private val districtRepository: DistrictRepository,
    private val municipalityMapper: MunicipalityMapper,
) : MunicipalityService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun createMunicipality(request: CreateMunicipalityRequest): MunicipalityResponse {
        logger.info("Creating municipality with code: ${request.code}")

        val district =
            districtRepository
                .findByCodeIgnoreCase(request.districtCode)
                .orElseThrow { DistrictNotFoundException(request.districtCode) }

        validateMunicipalityCode(request.code, request.districtCode)

        val municipality =
            Municipality().apply {
                name = request.name
                nameNepali = request.nameNepali
                code = request.code.uppercase()
                type = request.type
                area = request.area
                population = request.population
                latitude = request.latitude
                longitude = request.longitude
                totalWards = request.totalWards
                this.district = district
            }

        return municipalityRepository
            .save(municipality)
            .let { municipalityMapper.toResponse(it) }
            .also { logger.info("Created municipality with code: ${it.code}") }
    }

    @Transactional
    override fun updateMunicipality(
        code: String,
        request: UpdateMunicipalityRequest,
    ): MunicipalityResponse {
        logger.info("Updating municipality: $code")

        val municipality = getMunicipalityEntity(code)

        municipality.apply {
            request.name?.let { name = it }
            request.nameNepali?.let { nameNepali = it }
            request.area?.let { area = it }
            request.population?.let { population = it }
            request.latitude?.let { latitude = it }
            request.longitude?.let { longitude = it }
            request.totalWards?.let { totalWards = it }
            request.isActive?.let { isActive = it }
        }

        return municipalityRepository
            .save(municipality)
            .let { MunicipalityResponse.from(it) }
            .also { logger.info("Updated municipality with code: $code") }
    }

    @Transactional(readOnly = true)
    override fun getMunicipalityDetail(code: String): MunicipalityDetailResponse {
        logger.debug("Fetching detailed information for municipality: $code")

        val municipality = getMunicipalityEntity(code)
        val stats = calculateMunicipalityStats(municipality)

        return MunicipalityDetailResponse.from(municipality, stats)
    }

    @Transactional(readOnly = true)
    override fun searchMunicipalities(criteria: MunicipalitySearchCriteria): Page<MunicipalityResponse> {
        logger.debug("Searching municipalities with criteria: $criteria")

        criteria.validate()

        val specification = MunicipalitySpecifications.withSearchCriteria(criteria)
        val pageable =
            PageRequest.of(
                criteria.page,
                criteria.pageSize,
                Sort.by(criteria.sortDirection, criteria.sortBy.toEntityField()),
            )

        return municipalityRepository
            .findAll(specification, pageable)
            .map { municipalityMapper.toResponse(it) }
    }

    @Transactional(readOnly = true)
    override fun getMunicipality(code: String): MunicipalityResponse {
        logger.debug("Fetching municipality: $code")
        return getMunicipalityEntity(code)
            .let { MunicipalityResponse.from(it) }
    }

    @Transactional(readOnly = true)
    override fun getMunicipalitiesByDistrict(districtCode: String): List<MunicipalityResponse> {
        logger.debug("Fetching municipalities for district: $districtCode")

        if (!districtRepository.existsByCode(districtCode)) {
            throw DistrictNotFoundException(districtCode)
        }

        return municipalityRepository
            .findByDistrictCode(districtCode)
            .map { MunicipalityResponse.from(it) }
    }

    @Transactional(readOnly = true)
    override fun getMunicipalityStatistics(code: String): MunicipalityStats {
        logger.debug("Fetching statistics for municipality: $code")
        return getMunicipalityEntity(code)
            .let { calculateMunicipalityStats(it) }
    }

    @Transactional
    override fun deactivateMunicipality(code: String) {
        logger.info("Deactivating municipality: $code")

        val municipality = getMunicipalityEntity(code)

        // Check for active wards
        if (municipality.wards.any { it.isActive }) {
            throw MunicipalityOperationException(
                "Cannot deactivate municipality with active wards",
                "ACTIVE_WARDS_EXIST",
            )
        }

        municipality.isActive = false
        municipalityRepository.save(municipality)
        logger.info("Municipality $code deactivated successfully")
    }

    @Transactional
    override fun reactivateMunicipality(code: String) {
        logger.info("Reactivating municipality: $code")

        val municipality = getMunicipalityEntity(code)

        if (municipality.isActive) {
            throw MunicipalityOperationException(
                "Municipality is already active",
                "ALREADY_ACTIVE",
            )
        }

        municipality.isActive = true
        municipalityRepository.save(municipality)
        logger.info("Municipality $code reactivated successfully")
    }

    @Transactional(readOnly = true)
    override fun findNearbyMunicipalities(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): Page<MunicipalityResponse> {
        logger.debug("Finding municipalities within ${radiusKm}km of ($latitude, $longitude)")

        val criteria =
            MunicipalitySearchCriteria(
                latitude = latitude,
                longitude = longitude,
                radiusKm = radiusKm,
                page = page,
                pageSize = size,
                sortBy = MunicipalitySortField.DISTANCE,
            )

        return searchMunicipalities(criteria)
    }

    @Transactional(readOnly = true)
    override fun getMunicipalitiesByType(type: MunicipalityType): List<MunicipalityResponse> {
        logger.debug("Fetching municipalities of type: $type")
        return municipalityRepository
            .findByType(type)
            .map { MunicipalityResponse.from(it) }
    }

    @Transactional(readOnly = true)
    override fun getMunicipalityAnalytics(code: String): MunicipalityStats {
        logger.debug("Generating analytics for municipality: $code")

        val municipality = getMunicipalityEntity(code)
        validateMunicipalityAccess(municipality.code!!)

        return calculateDetailedMunicipalityStats(municipality)
    }

    @Transactional(readOnly = true)
    override fun validateMunicipalityAccess(municipalityCode: String) {
        logger.debug("Validating access for municipality: $municipalityCode")

        val municipality = getMunicipalityEntity(municipalityCode)
        val currentUser = currentUserService.getCurrentUser()

        if (!currentUser.isSuperAdmin() &&
            !currentUser.isMunicipalityAdmin() &&
            currentUser.municipalityCode != municipalityCode
        ) {
            throw MunicipalityOperationException(
                "User does not have access to this municipality",
                "ACCESS_DENIED",
            )
        }
    }

    private fun calculateDetailedMunicipalityStats(municipality: Municipality): MunicipalityStats =
        MunicipalityStats(
            totalWards = municipality.totalWards ?: 0,
            activeWards = municipality.wards.count { it.isActive },
            totalPopulation = municipality.population ?: 0,
            totalArea = municipality.area ?: BigDecimal.ZERO,
            totalFamilies = calculateTotalFamilies(municipality),
            wardStats = generateWardStatistics(municipality),
            demographicStats = generateDemographicStats(municipality),
            economicStats = generateEconomicStats(municipality),
            infrastructureStats = generateInfrastructureStats(municipality),
        )

    private fun calculateTotalFamilies(municipality: Municipality): Long {
        // TODO: Implement family counting across all wards
        return 0
    }

    private fun generateWardStatistics(municipality: Municipality): List<WardStatistics> {
        // TODO: Generate detailed ward-level statistics
        return emptyList()
    }

    private fun generateDemographicStats(municipality: Municipality): DemographicStats {
        // TODO: Generate demographic statistics
        return DemographicStats()
    }

    private fun generateEconomicStats(municipality: Municipality): EconomicStats {
        // TODO: Generate economic statistics
        return EconomicStats()
    }

    private fun generateInfrastructureStats(municipality: Municipality): InfrastructureStats {
        // TODO: Generate infrastructure statistics
        return InfrastructureStats()
    }

    // Private helper methods
    private fun getMunicipalityEntity(code: String): Municipality =
        municipalityRepository
            .findByCodeIgnoreCase(code)
            .orElseThrow { MunicipalityNotFoundException(code) }

    private fun getMunicipalityEntity(code: String): Municipality =
        municipalityRepository
            .findByCodeIgnoreCase(code)
            .orElseThrow { MunicipalityNotFoundException(code) }

    private fun validateMunicipalityCode(
        code: String,
        districtId: UUID,
    ) {
        if (municipalityRepository.existsByCodeAndDistrict(code, districtId, null)) {
            throw DuplicateMunicipalityCodeException(code, districtId)
        }
    }

    private fun validateMunicipalityCode(
        code: String,
        districtCode: String,
    ) {
        if (municipalityRepository.existsByCodeAndDistrict(code.uppercase(), districtCode)) {
            throw MunicipalityCodeExistsException(code)
        }
    }

    private fun calculateMunicipalityStats(municipality: Municipality): MunicipalityStats {
        // Implementation for calculating statistics
        // This would aggregate data from various related entities
        return MunicipalityStats(
            totalWards = municipality.totalWards ?: 0,
            activeWards = municipality.wards.count { it.isActive },
            totalPopulation = municipality.population ?: 0,
            totalArea = municipality.area ?: BigDecimal.ZERO,
            totalFamilies = 0, // TODO: Implement family counting
            wardStats = emptyList(), // TODO: Implement ward statistics
        )
    }

    private fun buildSpecification(criteria: MunicipalitySearchCriteria): Specification<Municipality> =
        Specification { root, query, builder ->
            val predicates = mutableListOf<javax.persistence.criteria.Predicate>()

            criteria.searchTerm?.let { term ->
                predicates.add(
                    builder.or(
                        builder.like(
                            builder.lower(root.get("name")),
                            "%${term.lowercase()}%",
                        ),
                        builder.like(
                            builder.lower(root.get("nameNepali")),
                            "%${term.lowercase()}%",
                        ),
                        builder.like(
                            builder.lower(root.get("code")),
                            "%${term.lowercase()}%",
                        ),
                    ),
                )
            }

            // Add more predicates based on criteria...

            builder.and(*predicates.toTypedArray())
        }

    private fun buildPageRequest(criteria: MunicipalitySearchCriteria): PageRequest {
        val sortField = criteria.sortBy.toEntityField()
        val direction = criteria.sortDirection
        return PageRequest.of(
            criteria.page,
            criteria.pageSize,
            Sort.by(direction, sortField),
        )
    }

    // Implement remaining interface methods...
}
