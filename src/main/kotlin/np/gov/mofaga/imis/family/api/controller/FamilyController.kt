package np.gov.mofaga.imis.family.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.mofaga.imis.family.api.dto.request.CreateFamilyRequest
import np.gov.mofaga.imis.family.api.dto.request.FamilySearchCriteria
import np.gov.mofaga.imis.family.api.dto.request.UpdateFamilyRequest
import np.gov.mofaga.imis.family.api.dto.response.FamilyResponse
import np.gov.mofaga.imis.family.service.FamilyService
import np.gov.mofaga.imis.shared.dto.ApiResponse
import np.gov.mofaga.imis.shared.dto.toApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/families")
@Tag(name = "Family Management", description = "APIs for managing family information")
@Validated
@PreAuthorize("isAuthenticated()")
class FamilyController(
    private val familyService: FamilyService,
) {
    @Operation(summary = "Create new family")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR')")
    fun createFamily(
        @Valid @RequestBody request: CreateFamilyRequest,
    ): ResponseEntity<ApiResponse<FamilyResponse>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                ApiResponse.success(
                    data = familyService.createFamily(request),
                    message = "Family created successfully",
                ),
            )

    @Operation(summary = "Update family details")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR')")
    fun updateFamily(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateFamilyRequest,
    ): ResponseEntity<ApiResponse<FamilyResponse>> =
        ResponseEntity.ok(
            ApiResponse.success(
                data = familyService.updateFamily(id, request),
                message = "Family updated successfully",
            ),
        )

    @Operation(summary = "Get family details")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR', 'VIEWER')")
    fun getFamily(
        @PathVariable id: UUID,
    ): ResponseEntity<ApiResponse<FamilyResponse>> =
        ResponseEntity.ok(
            ApiResponse.success(
                data = familyService.getFamily(id),
            ),
        )

    @Operation(summary = "Search families")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR', 'VIEWER')")
    fun searchFamilies(
        @Valid criteria: FamilySearchCriteria,
    ): ResponseEntity<ApiResponse<List<FamilyResponse>>> {
        val searchResults = familyService.searchFamilies(criteria)
        return ResponseEntity.ok(
            searchResults.toApiResponse(
                message = "Found ${searchResults.totalElements} families",
            ),
        )
    }

    @Operation(summary = "Delete family")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR')")
    fun deleteFamily(
        @PathVariable id: UUID,
    ): ResponseEntity<ApiResponse<Unit>> =
        familyService.deleteFamily(id).let {
            ResponseEntity.ok(
                ApiResponse.success(
                    message = "Family deleted successfully",
                ),
            )
        }
}
