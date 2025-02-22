package np.gov.mofaga.imis.location.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.mofaga.imis.location.api.dto.criteria.WardSearchCriteria
import np.gov.mofaga.imis.location.api.dto.request.CreateWardRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateWardRequest
import np.gov.mofaga.imis.location.api.dto.response.WardDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.WardResponse
import np.gov.mofaga.imis.location.api.dto.response.WardSummaryResponse
import np.gov.mofaga.imis.location.service.WardService
import np.gov.mofaga.imis.shared.dto.ApiResponse
import np.gov.mofaga.imis.shared.dto.PagedResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

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
            SwaggerApiResponse(responseCode = "200", description = "Ward created successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Invalid input data"),
            SwaggerApiResponse(responseCode = "403", description = "Insufficient permissions"),
            SwaggerApiResponse(responseCode = "404", description = "Municipality not found"),
            SwaggerApiResponse(responseCode = "409", description = "Ward number already exists in municipality"),
        ],
    )
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
            SwaggerApiResponse(responseCode = "200", description = "Ward updated successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Invalid input data"),
            SwaggerApiResponse(responseCode = "403", description = "Insufficient permissions"),
            SwaggerApiResponse(responseCode = "404", description = "Ward not found"),
        ],
    )
    @PutMapping("/{municipalityCode}/{wardNumber}")
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'SUPER_ADMIN')")
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
}
