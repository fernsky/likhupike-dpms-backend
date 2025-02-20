package np.gov.likhupikemun.dpms.location.service.impl

import np.gov.likhupikemun.dpms.auth.service.AuthenticationService
import np.gov.likhupikemun.dpms.family.repository.FamilyRepository
import np.gov.likhupikemun.dpms.location.api.dto.criteria.WardSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.mapper.WardMapper
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.*
import np.gov.likhupikemun.dpms.location.domain.Ward
import np.gov.likhupikemun.dpms.location.exception.*
import np.gov.likhupikemun.dpms.location.repository.MunicipalityRepository
import np.gov.likhupikemun.dpms.location.repository.WardRepository
import np.gov.likhupikemun.dpms.location.repository.specification.WardSpecifications
import np.gov.likhupikemun.dpms.location.service.WardService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
class WardServiceImpl(
    private val wardRepository: WardRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val familyRepository: FamilyRepository,
    private val authenticationService: AuthenticationService,
    private val wardMapper: WardMapper,
) : WardService {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    @Transactional
    override fun createWard(request: CreateWardRequest): WardResponse {
        logger.info("Creating ward ${request.wardNumber} for municipality: ${request.municipalityCode}")

        val municipality =
            municipalityRepository
                .findByCodeIgnoreCase(request.municipalityCode)
                .orElseThrow { MunicipalityNotFoundException(request.municipalityCode) }

        validateWardNumber(request.wardNumber, request.municipalityCode)

        val ward =
            Ward().apply {
                this.wardNumber = request.wardNumber
                this.municipality = municipality
                this.area = request.area
                this.population = request.population
                this.latitude = request.latitude
                this.longitude = request.longitude
                this.officeLocation = request.officeLocation
                this.officeLocationNepali = request.officeLocationNepali
                this.isActive = true
            }

        return wardRepository
            .save(ward)
            .let { wardMapper.toResponse(it) }
            .also { logger.info("Created ward ${it.wardNumber} in ${it.municipalityCode}") }
    }

    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    @Transactional
    override fun updateWard(
        municipalityCode: String,
        wardNumber: Int,
        request: UpdateWardRequest,
    ): WardResponse {
        val ward = getWardEntity(wardNumber, municipalityCode)
        validateWardAccess(municipalityCode, wardNumber)

        ward.apply {
            area = request.area
            population = request.population
            latitude = request.latitude
            longitude = request.longitude
            officeLocation = request.officeLocation
            officeLocationNepali = request.officeLocationNepali
            updatedAt = LocalDateTime.now()
        }

        return wardMapper
            .toResponse(wardRepository.save(ward))
            .also { logger.info("Updated ward ${it.wardNumber} in ${it.municipalityCode}") }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    override fun getWardDetail(
        municipalityCode: String,
        wardNumber: Int,
    ): WardDetailResponse {
        val ward = getWardEntity(wardNumber, municipalityCode)
        return wardMapper.toDetailResponse(
            ward = ward,
        )
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    override fun getWard(
        municipalityCode: String,
        wardNumber: Int,
    ): WardResponse = wardMapper.toResponse(getWardEntity(wardNumber, municipalityCode))

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    override fun searchWards(criteria: WardSearchCriteria): Page<WardResponse> {
        logger.debug("Searching wards with criteria: $criteria")

        criteria.validate()
        return wardRepository
            .findAll(
                WardSpecifications.withSearchCriteria(criteria),
                PageRequest.of(criteria.page, criteria.pageSize, criteria.getSort()),
            ).map(wardMapper::toResponse)
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    override fun getWardsByMunicipality(municipalityCode: String): List<WardSummaryResponse> =
        wardRepository
            .findByMunicipalityCodeAndIsActive(municipalityCode, true)
            .map(wardMapper::toSummaryResponse)

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    override fun findNearbyWards(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusKm: Double,
        page: Int,
        size: Int,
    ): Page<WardSummaryResponse> {
        val criteria =
            WardSearchCriteria(
                latitude = latitude,
                longitude = longitude,
                radiusKm = radiusKm,
                page = page,
                pageSize = size,
            )

        return searchWards(criteria).map { wardMapper.toSummaryResponse(it) }
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    override fun validateWardAccess(
        municipalityCode: String,
        wardNumber: Int,
    ) {
        val ward = getWardEntity(wardNumber, municipalityCode)
        val currentUser = authenticationService.getCurrentUser()

        if (!currentUser.isMunicipalityAdmin() &&
            currentUser.wardNumber != ward.wardNumber
        ) {
            throw InvalidWardOperationException()
        }
    }

    private fun getWardEntity(
        wardNumber: Int,
        municipalityCode: String,
    ): Ward =
        wardRepository
            .findByWardNumberAndMunicipalityCode(wardNumber, municipalityCode)
            .orElseThrow { WardNotFoundException(wardNumber, municipalityCode) }

    private fun validateWardNumber(
        wardNumber: Int,
        municipalityCode: String,
    ) {
        if (wardRepository.existsByWardNumberAndMunicipalityCode(wardNumber, municipalityCode)) {
            throw DuplicateWardNumberException(wardNumber, municipalityCode)
        }
    }
}
