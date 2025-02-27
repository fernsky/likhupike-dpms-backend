package np.gov.mofaga.imis.location.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.mofaga.imis.location.api.dto.enums.ProvinceField
import np.gov.mofaga.imis.location.api.dto.request.CreateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.request.UpdateProvinceRequest
import np.gov.mofaga.imis.location.api.dto.response.DynamicProvinceProjection
import np.gov.mofaga.imis.location.api.dto.response.ProvinceDetailResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceResponse
import np.gov.mofaga.imis.location.service.ProvinceService
import np.gov.mofaga.imis.shared.dto.ApiResponse
import np.gov.mofaga.imis.shared.dto.toApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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

    @Operation(summary = "Create a new province")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun createProvince(
        @Valid @RequestBody request: CreateProvinceRequest,
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

    @Operation(summary = "Search provinces")
    @GetMapping("/search")
    fun searchProvinces(
        @Valid criteria: ProvinceSearchCriteria,
        @RequestParam(required = false) fields: String?,
    ): ResponseEntity<ApiResponse<List<DynamicProvinceProjection>>> {
        logger.debug("Raw fields parameter: $fields")

        val selectedFields =
            fields?.let {
                it
                    .split(",")
                    .map { field -> ProvinceField.valueOf(field.trim().uppercase()) }
                    .toSet()
            } ?: ProvinceField.DEFAULT_FIELDS

        val searchCriteria = criteria.copy(fields = selectedFields)
        val searchResults = provinceService.searchProvinces(searchCriteria)

        return ResponseEntity.ok(
            searchResults.toApiResponse(
                message = "Found ${searchResults.totalElements} provinces",
            ),
        )
    }

    @Operation(summary = "Find large provinces")
    @GetMapping("/large")
    fun findLargeProvinces(
        @RequestParam minArea: BigDecimal,
        @RequestParam minPopulation: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<ApiResponse<List<ProvinceResponse>>> {
        logger.debug("Finding large provinces with minArea: $minArea, minPopulation: $minPopulation")
        val provinces = provinceService.findLargeProvinces(minArea, minPopulation, page, size)
        return ResponseEntity.ok(provinces.toApiResponse())
    }

    // For non-paginated endpoints, use simple success response
    @Operation(summary = "Get province details")
    @GetMapping("/{code}")
    fun getProvinceDetail(
        @PathVariable code: String,
    ): ResponseEntity<ApiResponse<ProvinceDetailResponse>> {
        logger.debug("Fetching province detail: $code")
        val provinceDetail = provinceService.getProvinceDetail(code)
        return ResponseEntity.ok(ApiResponse.success(data = provinceDetail))
    }

    @Operation(summary = "Update province")
    @PutMapping("/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    fun updateProvince(
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

    @Operation(summary = "Get all provinces")
    @GetMapping
    fun getAllProvinces(): ResponseEntity<ApiResponse<List<ProvinceResponse>>> {
        logger.debug("Fetching all provinces")
        val provinces = provinceService.getAllProvinces()
        return ResponseEntity.ok(ApiResponse.success(data = provinces))
    }
}
