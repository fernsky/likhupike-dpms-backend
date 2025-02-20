package np.gov.likhupikemun.dpms.location.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateDistrictRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.DistrictStats
import np.gov.likhupikemun.dpms.location.service.DistrictService
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.PagedResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/districts")
@Tag(name = "District Management", description = "APIs for managing districts of Nepal")
@Validated
class DistrictController(
    private val districtService: DistrictService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "Create a new district",
        description = "Creates a new district. Only accessible by super admins.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "District created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            ApiResponse(responseCode = "409", description = "District code already exists"),
        ],
    )
    @PostMapping
    fun createDistrict(
        @Parameter(description = "District creation details", required = true)
        @Valid
        @RequestBody request: CreateDistrictRequest,
    ): ResponseEntity<ApiResponse<DistrictResponse>> {
        logger.info("Creating district with code: ${request.code}")
        val createdDistrict = districtService.createDistrict(request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = createdDistrict,
                message = "District created successfully",
            ),
        )
    }

    @Operation(summary = "Get district details", description = "Get detailed information about a specific district")
    @GetMapping("/{code}")
    fun getDistrictDetail(
        @Parameter(description = "District code", required = true)
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<DistrictDetailResponse>> {
        logger.debug("Fetching district detail: $code")
        val districtDetail = districtService.getDistrictDetail(code)
        return ResponseEntity.ok(ApiResponse.success(data = districtDetail))
    }

    @Operation(summary = "Search districts", description = "Search districts with various filters and criteria")
    @GetMapping("/search")
    fun searchDistricts(
        @Parameter(description = "Search criteria")
        @Valid criteria: DistrictSearchCriteria,
    ): ResponseEntity<ApiResponse<PagedResponse<DistrictResponse>>> {
        logger.debug("Searching districts with criteria: $criteria")
        val searchResults = districtService.searchDistricts(criteria)
        return ResponseEntity.ok(
            ApiResponse.success(data = PagedResponse.from(searchResults)),
        )
    }

    @Operation(
        summary = "Update district",
        description = "Updates an existing district. Only accessible by super admins.",
    )
    @PutMapping("/{code}")
    fun updateDistrict(
        @Parameter(description = "District code", required = true)
        @PathVariable code: String,
        @Valid @RequestBody request: UpdateDistrictRequest,
    ): ResponseEntity<ApiResponse<DistrictResponse>> {
        logger.info("Updating district: $code")
        val updatedDistrict = districtService.updateDistrict(code, request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = updatedDistrict,
                message = "District updated successfully",
            ),
        )
    }

    @Operation(summary = "Get district statistics", description = "Get statistical information about a district")
    @GetMapping("/{code}/statistics")
    fun getDistrictStatistics(
        @Parameter(description = "District code", required = true)
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<DistrictStats>> {
        logger.debug("Fetching statistics for district: $code")
        val stats = districtService.getDistrictStatistics(code)
        return ResponseEntity.ok(ApiResponse.success(data = stats))
    }

    @Operation(summary = "Get all districts", description = "Get list of all districts")
    @GetMapping
    fun getAllDistricts(): ResponseEntity<ApiResponse<List<DistrictResponse>>> {
        logger.debug("Fetching all districts")
        val districts = districtService.getAllDistricts()
        return ResponseEntity.ok(ApiResponse.success(data = districts))
    }

    @Operation(
        summary = "Get districts by province",
        description = "Get list of districts in a specific province",
    )
    @GetMapping("/by-province/{provinceCode}")
    fun getDistrictsByProvince(
        @Parameter(description = "Province code", required = true)
        @PathVariable provinceCode: String,
    ): ResponseEntity<ApiResponse<List<DistrictResponse>>> {
        logger.debug("Fetching districts for province: $provinceCode")
        val districts = districtService.getDistrictsByProvince(provinceCode)
        return ResponseEntity.ok(ApiResponse.success(data = districts))
    }
}
