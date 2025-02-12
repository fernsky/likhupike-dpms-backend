package np.gov.likhupikemun.dpms.family.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.family.api.dto.request.CreateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.request.FamilySearchCriteria
import np.gov.likhupikemun.dpms.family.api.dto.request.UpdateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.response.FamilyResponse
import np.gov.likhupikemun.dpms.family.service.FamilyService
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.PagedResponse
import org.springframework.data.domain.Page
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
    @Operation(
        summary = "Create new family",
        description = "Create a new family record. Only super admin, municipality admin, ward admin and editors can create families."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR')")
    fun createFamily(
        @Parameter(description = "Family creation details", required = true)
        @Valid @RequestBody request: CreateFamilyRequest,
    ): ResponseEntity<ApiResponse<FamilyResponse>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(data = familyService.createFamily(request)))

    @Operation(
        summary = "Update family details",
        description = "Update an existing family's information. Only super admin, municipality admin, ward admin and editors can update families."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR')")
    fun updateFamily(
        @Parameter(description = "Family ID", required = true)
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateFamilyRequest,
    ): ResponseEntity<ApiResponse<FamilyResponse>> =
        ResponseEntity.ok(ApiResponse.success(data = familyService.updateFamily(id, request)))

    @Operation(
        summary = "Get family details",
        description = "Retrieve details of a specific family. All authenticated users including viewers can access this endpoint."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR', 'VIEWER')")
    fun getFamily(
        @Parameter(description = "Family ID", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<ApiResponse<FamilyResponse>> =
        ResponseEntity.ok(ApiResponse.success(data = familyService.getFamily(id)))

    @Operation(
        summary = "Search families",
        description = "Search and filter families based on various criteria. All authenticated users including viewers can access this endpoint."
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR', 'VIEWER')")
    fun searchFamilies(
        @Parameter(description = "Search criteria with filters")
        @Valid criteria: FamilySearchCriteria
    ): ResponseEntity<ApiResponse<PagedResponse<FamilyResponse>>> {
        val searchResults = familyService.searchFamilies(criteria)
        val pagedResponse = PagedResponse.from(searchResults)
        return ResponseEntity.ok(ApiResponse.success(data = pagedResponse))
    }

    @Operation(
        summary = "Delete family",
        description = "Delete a family record. Only super admin, municipality admin, ward admin and editors can delete families."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'EDITOR')")
    fun deleteFamily(
        @Parameter(description = "Family ID", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<ApiResponse<Unit>> =
        familyService.deleteFamily(id).let {
            ResponseEntity.ok(ApiResponse.success(message = "Family deleted successfully"))
        }
}
