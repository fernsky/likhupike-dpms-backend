package np.gov.mofaga.imis.location.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.mofaga.imis.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.mofaga.imis.location.api.dto.enums.MunicipalityField
import np.gov.mofaga.imis.location.api.dto.request.CreateMunicipalityRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateMunicipalityRequest
import np.gov.mofaga.imis.location.api.dto.response.DynamicMunicipalityProjection
import np.gov.mofaga.imis.location.api.dto.response.MunicipalityDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.MunicipalityResponse
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.location.service.MunicipalityService
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
            SwaggerApiResponse(responseCode = "200", description = "Municipality created successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Invalid input data"),
            SwaggerApiResponse(responseCode = "403", description = "Insufficient permissions"),
            SwaggerApiResponse(responseCode = "409", description = "Municipality code already exists"),
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
            SwaggerApiResponse(responseCode = "200", description = "Municipality updated successfully"),
            SwaggerApiResponse(responseCode = "404", description = "Municipality not found"),
            SwaggerApiResponse(responseCode = "403", description = "Insufficient permissions"),
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
        @Parameter(description = "Comma-separated list of fields to include")
        @RequestParam(required = false) fields: String?,
    ): ResponseEntity<ApiResponse<PagedResponse<DynamicMunicipalityProjection>>> {
        logger.debug("Raw fields parameter: $fields")

        val selectedFields =
            fields?.let {
                it
                    .split(",")
                    .map { field -> MunicipalityField.valueOf(field.trim().uppercase()) }
                    .toSet()
            } ?: MunicipalityField.DEFAULT_FIELDS

        val searchCriteria = criteria.copy(fields = selectedFields)
        val searchResults = municipalityService.searchMunicipalities(searchCriteria)

        return ResponseEntity.ok(
            ApiResponse.success(
                data = PagedResponse.from(searchResults),
                message = "Found ${searchResults.totalElements} municipalities",
            ),
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

        val criteria =
            MunicipalitySearchCriteria(
                latitude = latitude,
                longitude = longitude,
                radiusKm = radiusKm,
                page = page,
                pageSize = size,
            )

        val nearbyMunicipalities = municipalityService.findNearbyMunicipalities(criteria)
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
