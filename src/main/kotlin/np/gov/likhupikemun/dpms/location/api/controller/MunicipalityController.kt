package np.gov.likhupikemun.dpms.location.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.MunicipalityStats
import np.gov.likhupikemun.dpms.location.domain.MunicipalityType
import np.gov.likhupikemun.dpms.location.service.MunicipalityService
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.PagedResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/municipalities")
@Tag(name = "Municipality Management", description = "APIs for managing municipalities")
@Validated
class MunicipalityController(
    private val municipalityService: MunicipalityService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Create a new municipality")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Municipality created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            ApiResponse(responseCode = "409", description = "Municipality code already exists"),
        ],
    )
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun createMunicipality(
        @Parameter(description = "Municipality creation details", required = true)
        @Valid
        @RequestBody request: CreateMunicipalityRequest,
    ): ResponseEntity<ApiResponse<MunicipalityResponse>> {
        logger.info("Creating municipality with code: ${request.code}")
        val createdMunicipality = municipalityService.createMunicipality(request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = createdMunicipality,
                message = "Municipality created successfully",
            ),
        )
    }

    @Operation(summary = "Update an existing municipality")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Municipality updated successfully"),
            ApiResponse(responseCode = "404", description = "Municipality not found"),
            ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        ],
    )
    @PutMapping("/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun updateMunicipality(
        @Parameter(description = "Municipality code", required = true)
        @PathVariable code: String,
        @Parameter(description = "Municipality update details", required = true)
        @Valid
        @RequestBody request: UpdateMunicipalityRequest,
    ): ResponseEntity<ApiResponse<MunicipalityResponse>> {
        logger.info("Updating municipality: $code")
        val updatedMunicipality = municipalityService.updateMunicipality(code, request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = updatedMunicipality,
                message = "Municipality updated successfully",
            ),
        )
    }

    @Operation(summary = "Get detailed municipality information")
    @GetMapping("/{code}")
    fun getMunicipalityDetail(
        @Parameter(description = "Municipality code", required = true)
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<MunicipalityDetailResponse>> {
        logger.debug("Fetching municipality detail: $code")
        val municipalityDetail = municipalityService.getMunicipalityDetail(code)
        return ResponseEntity.ok(ApiResponse.success(data = municipalityDetail))
    }

    @Operation(summary = "Search municipalities with filters")
    @GetMapping("/search")
    fun searchMunicipalities(
        @Parameter(description = "Search criteria")
        @Valid criteria: MunicipalitySearchCriteria,
    ): ResponseEntity<ApiResponse<PagedResponse<MunicipalityResponse>>> {
        logger.debug("Searching municipalities with criteria: $criteria")
        val searchResults = municipalityService.searchMunicipalities(criteria)
        return ResponseEntity.ok(
            ApiResponse.success(data = PagedResponse.from(searchResults)),
        )
    }

    @Operation(summary = "Get municipalities by district")
    @GetMapping("/by-district/{districtCode}")
    fun getMunicipalitiesByDistrict(
        @Parameter(description = "District code", required = true)
        @PathVariable districtCode: String,
    ): ResponseEntity<ApiResponse<List<MunicipalityResponse>>> {
        logger.debug("Fetching municipalities for district: $districtCode")
        val municipalities = municipalityService.getMunicipalitiesByDistrict(districtCode)
        return ResponseEntity.ok(ApiResponse.success(data = municipalities))
    }

    @Operation(summary = "Get municipalities by type")
    @GetMapping("/by-type/{type}")
    fun getMunicipalitiesByType(
        @Parameter(description = "Municipality type", required = true)
        @PathVariable type: MunicipalityType,
    ): ResponseEntity<ApiResponse<List<MunicipalityResponse>>> {
        logger.debug("Fetching municipalities of type: $type")
        val municipalities = municipalityService.getMunicipalitiesByType(type)
        return ResponseEntity.ok(ApiResponse.success(data = municipalities))
    }

    @Operation(summary = "Get municipality statistics")
    @GetMapping("/{code}/statistics")
    fun getMunicipalityStatistics(
        @Parameter(description = "Municipality code", required = true)
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<MunicipalityStats>> {
        logger.debug("Fetching statistics for municipality: $code")
        val stats = municipalityService.getMunicipalityStatistics(code)
        return ResponseEntity.ok(ApiResponse.success(data = stats))
    }

    @Operation(summary = "Find nearby municipalities")
    @GetMapping("/nearby")
    fun findNearbyMunicipalities(
        @Parameter(description = "Latitude", required = true)
        @RequestParam latitude: BigDecimal,
        @Parameter(description = "Longitude", required = true)
        @RequestParam longitude: BigDecimal,
        @Parameter(description = "Search radius in kilometers", required = true)
        @RequestParam radiusKm: Double,
        @Parameter(description = "Page number")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<ApiResponse<PagedResponse<MunicipalityResponse>>> {
        logger.debug("Finding municipalities within ${radiusKm}km of ($latitude, $longitude)")
        val nearbyMunicipalities =
            municipalityService.findNearbyMunicipalities(
                latitude,
                longitude,
                radiusKm,
                page,
                size,
            )
        return ResponseEntity.ok(
            ApiResponse.success(data = PagedResponse.from(nearbyMunicipalities)),
        )
    }

    @Operation(summary = "Get all municipalities")
    @GetMapping
    fun getAllMunicipalities(): ResponseEntity<ApiResponse<List<MunicipalityResponse>>> {
        logger.debug("Fetching all municipalities")
        val municipalities = municipalityService.getAllMunicipalities()
        return ResponseEntity.ok(ApiResponse.success(data = municipalities))
    }
}
