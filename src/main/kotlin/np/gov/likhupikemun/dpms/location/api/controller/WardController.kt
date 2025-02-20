package np.gov.likhupikemun.dpms.location.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.location.api.dto.criteria.WardSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateWardRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.WardDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.WardResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.WardStats
import np.gov.likhupikemun.dpms.location.api.dto.response.WardSummaryResponse
import np.gov.likhupikemun.dpms.location.service.WardService
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.PagedResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/wards")
@Tag(name = "Ward Management", description = "APIs for managing municipality wards")
@Validated
class WardController(
    private val wardService: WardService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Create a new ward")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Ward created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            ApiResponse(responseCode = "404", description = "Municipality not found"),
            ApiResponse(responseCode = "409", description = "Ward number already exists in municipality"),
        ],
    )
    @PostMapping
    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    fun createWard(
        @Parameter(description = "Ward creation details", required = true)
        @Valid
        @RequestBody request: CreateWardRequest,
    ): ResponseEntity<ApiResponse<WardResponse>> {
        logger.info("Creating ward ${request.wardNumber} for municipality ${request.municipalityCode}")
        val createdWard = wardService.createWard(request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = createdWard,
                message = "Ward created successfully",
            ),
        )
    }

    @Operation(summary = "Update an existing ward")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Ward updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            ApiResponse(responseCode = "404", description = "Ward not found"),
        ],
    )
    @PutMapping("/{municipalityCode}/{wardNumber}")
    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    fun updateWard(
        @Parameter(description = "Municipality code", required = true)
        @PathVariable municipalityCode: String,
        @Parameter(description = "Ward number", required = true)
        @PathVariable wardNumber: Int,
        @Parameter(description = "Ward update details", required = true)
        @Valid
        @RequestBody request: UpdateWardRequest,
    ): ResponseEntity<ApiResponse<WardResponse>> {
        logger.info("Updating ward $wardNumber in municipality $municipalityCode")
        val updatedWard = wardService.updateWard(wardNumber, municipalityCode, request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = updatedWard,
                message = "Ward updated successfully",
            ),
        )
    }

    @Operation(summary = "Get detailed ward information")
    @GetMapping("/{municipalityCode}/{wardNumber}")
    fun getWardDetail(
        @Parameter(description = "Municipality code", required = true)
        @PathVariable municipalityCode: String,
        @Parameter(description = "Ward number", required = true)
        @PathVariable wardNumber: Int,
    ): ResponseEntity<ApiResponse<WardDetailResponse>> {
        logger.debug("Fetching ward $wardNumber detail in municipality $municipalityCode")
        val wardDetail = wardService.getWardDetail(wardNumber, municipalityCode)
        return ResponseEntity.ok(ApiResponse.success(data = wardDetail))
    }

    @Operation(summary = "Search wards with filters")
    @GetMapping("/search")
    fun searchWards(
        @Parameter(description = "Search criteria")
        @Valid criteria: WardSearchCriteria,
    ): ResponseEntity<ApiResponse<PagedResponse<WardResponse>>> {
        logger.debug("Searching wards with criteria: $criteria")
        val searchResults = wardService.searchWards(criteria)
        return ResponseEntity.ok(
            ApiResponse.success(data = PagedResponse.from(searchResults)),
        )
    }

    @Operation(summary = "Get wards by municipality")
    @GetMapping("/by-municipality/{municipalityCode}")
    fun getWardsByMunicipality(
        @Parameter(description = "Municipality code", required = true)
        @PathVariable municipalityCode: String,
    ): ResponseEntity<ApiResponse<List<WardSummaryResponse>>> {
        logger.debug("Fetching wards for municipality: $municipalityCode")
        val wards = wardService.getWardsByMunicipality(municipalityCode)
        return ResponseEntity.ok(ApiResponse.success(data = wards))
    }

    @Operation(summary = "Get ward statistics")
    @GetMapping("/{municipalityCode}/{wardNumber}/statistics")
    fun getWardStatistics(
        @Parameter(description = "Municipality code", required = true)
        @PathVariable municipalityCode: String,
        @Parameter(description = "Ward number", required = true)
        @PathVariable wardNumber: Int,
    ): ResponseEntity<ApiResponse<WardStats>> {
        logger.debug("Fetching statistics for ward $wardNumber in municipality $municipalityCode")
        val stats = wardService.getWardStatistics(wardNumber, municipalityCode)
        return ResponseEntity.ok(ApiResponse.success(data = stats))
    }

    @Operation(summary = "Find nearby wards")
    @GetMapping("/nearby")
    fun findNearbyWards(
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
    ): ResponseEntity<ApiResponse<PagedResponse<WardSummaryResponse>>> {
        logger.debug("Finding wards within ${radiusKm}km of ($latitude, $longitude)")
        val nearbyWards = wardService.findNearbyWards(latitude, longitude, radiusKm, page, size)
        return ResponseEntity.ok(
            ApiResponse.success(data = PagedResponse.from(nearbyWards)),
        )
    }

    @Operation(
        summary = "Deactivate ward",
        description = "Deactivate a ward. Cannot deactivate if ward has active families",
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    fun deactivateWard(
        @Parameter(description = "Ward ID", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.info("Deactivating ward: $id")
        wardService.deactivateWard(id)
        return ResponseEntity.ok(
            ApiResponse.success(message = "Ward deactivated successfully"),
        )
    }

    @Operation(summary = "Reactivate ward")
    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('MUNICIPALITY_ADMIN')")
    fun reactivateWard(
        @Parameter(description = "Ward ID", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.info("Reactivating ward: $id")
        wardService.reactivateWard(id)
        return ResponseEntity.ok(
            ApiResponse.success(message = "Ward reactivated successfully"),
        )
    }
}
