package np.gov.likhupikemun.dpms.location.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.likhupikemun.dpms.location.api.dto.request.CreateProvinceRequest
import np.gov.likhupikemun.dpms.location.api.dto.request.UpdateProvinceRequest
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceDetailResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceResponse
import np.gov.likhupikemun.dpms.location.api.dto.response.ProvinceStats
import np.gov.likhupikemun.dpms.location.service.ProvinceService
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.PagedResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/provinces")
@Tag(name = "Province Management", description = "APIs for managing provinces of Nepal")
@Validated
class ProvinceController(
    private val provinceService: ProvinceService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(
        summary = "Create a new province",
        description = "Creates a new province. Only accessible by super admins.",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Province created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            ApiResponse(responseCode = "409", description = "Province code already exists"),
        ],
    )
    @PostMapping
    fun createProvince(
        @Parameter(description = "Province creation details", required = true)
        @Valid
        @RequestBody request: CreateProvinceRequest,
    ): ResponseEntity<ApiResponse<ProvinceResponse>> {
        logger.info("Creating province with code: ${request.code}")
        val createdProvince = provinceService.createProvince(request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = createdProvince,
                message = "Province created successfully",
            ),
        )
    }

    @Operation(summary = "Get province details", description = "Get detailed information about a specific province")
    @GetMapping("/{code}")
    fun getProvinceDetail(
        @Parameter(description = "Province code", required = true)
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<ProvinceDetailResponse>> {
        logger.debug("Fetching province detail: $code")
        val provinceDetail = provinceService.getProvinceDetail(code)
        return ResponseEntity.ok(ApiResponse.success(data = provinceDetail))
    }

    @Operation(summary = "Search provinces", description = "Search provinces with various filters and criteria")
    @GetMapping("/search")
    fun searchProvinces(
        @Parameter(description = "Search criteria")
        @Valid criteria: ProvinceSearchCriteria,
    ): ResponseEntity<ApiResponse<PagedResponse<ProvinceResponse>>> {
        logger.debug("Searching provinces with criteria: $criteria")
        val searchResults = provinceService.searchProvinces(criteria)
        return ResponseEntity.ok(
            ApiResponse.success(data = PagedResponse.from(searchResults)),
        )
    }

    @Operation(
        summary = "Update province",
        description = "Updates an existing province. Only accessible by super admins.",
    )
    @PutMapping("/{code}")
    fun updateProvince(
        @Parameter(description = "Province code", required = true)
        @PathVariable code: String,
        @Valid @RequestBody request: UpdateProvinceRequest,
    ): ResponseEntity<ApiResponse<ProvinceResponse>> {
        logger.info("Updating province: $code")
        val updatedProvince = provinceService.updateProvince(code, request)
        return ResponseEntity.ok(
            ApiResponse.success(
                data = updatedProvince,
                message = "Province updated successfully",
            ),
        )
    }

    @Operation(summary = "Get province statistics", description = "Get statistical information about a province")
    @GetMapping("/{code}/statistics")
    fun getProvinceStatistics(
        @Parameter(description = "Province code", required = true)
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<ProvinceStats>> {
        logger.debug("Fetching statistics for province: $code")
        val stats = provinceService.getProvinceStatistics(code)
        return ResponseEntity.ok(ApiResponse.success(data = stats))
    }

    @Operation(summary = "Get all provinces", description = "Get list of all provinces")
    @GetMapping
    fun getAllProvinces(): ResponseEntity<ApiResponse<List<ProvinceResponse>>> {
        logger.debug("Fetching all provinces")
        val provinces = provinceService.getAllProvinces()
        return ResponseEntity.ok(ApiResponse.success(data = provinces))
    }

    @Operation(
        summary = "Find large provinces",
        description = "Find provinces based on minimum area and population criteria",
    )
    @GetMapping("/large")
    fun findLargeProvinces(
        @Parameter(description = "Minimum area in square kilometers")
        @RequestParam minArea: BigDecimal,
        @Parameter(description = "Minimum population")
        @RequestParam minPopulation: Long,
        @Parameter(description = "Page number")
        @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size")
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<ApiResponse<PagedResponse<ProvinceResponse>>> {
        logger.debug("Finding large provinces with minArea: $minArea, minPopulation: $minPopulation")
        val provinces = provinceService.findLargeProvinces(minArea, minPopulation, page, size)
        return ResponseEntity.ok(
            ApiResponse.success(data = PagedResponse.from(provinces)),
        )
    }
}
