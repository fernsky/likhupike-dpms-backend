package np.gov.likhupikemun.dpms.auth.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.auth.api.dto.request.CreateUserRequest
import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSearchCriteria
import np.gov.likhupikemun.dpms.auth.api.dto.response.UserResponse
import np.gov.likhupikemun.dpms.auth.service.UserService
import np.gov.likhupikemun.dpms.shared.dto.ApiResponse
import np.gov.likhupikemun.dpms.shared.dto.PagedResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing municipality and ward users")
@Validated
@PreAuthorize("isAuthenticated()")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN')")
    fun createUser(
        @Parameter(description = "User creation details", required = true)
        @Validated
        @RequestBody request: CreateUserRequest,
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val createdUser = userService.createUser(request)
        return ResponseEntity.ok(ApiResponse.success(data = createdUser))
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN', 'VIEWER')")
    fun searchUsers(
        @Parameter(description = "Search criteria with filters")
        @Valid criteria: UserSearchCriteria,
    ): ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> {
        val searchResults = userService.searchUsers(criteria)
        val pagedResponse: PagedResponse<UserResponse> = PagedResponse.from(searchResults)
        return ResponseEntity.ok(ApiResponse.success(data = pagedResponse))
    }

    @Operation(
        summary = "Approve user registration",
        description = """
            Approve a pending user registration.
            Municipality admins can approve any user.
            Ward admins can only approve users in their ward.
        """,
    )
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "User approved successfully",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "User not found",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "Insufficient permissions to approve user",
            ),
        ],
    )
    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN')")
    fun approveUser(
        @Parameter(description = "ID of user to approve", required = true)
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<UserResponse>> =
        ResponseEntity.ok(
            ApiResponse.success(
                data = userService.approveUser(userId),
                message = "User approved successfully",
            ),
        )

    @Operation(
        summary = "Deactivate user",
        description = """
            Deactivate an existing user account.
            Municipality admins can deactivate any user.
            Ward admins can only deactivate users in their ward.
            Cannot deactivate municipality admin unless you are a municipality admin.
        """,
    )
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "User deactivated successfully",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "User not found",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "Insufficient permissions to deactivate user",
            ),
        ],
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN')")
    fun deactivateUser(
        @Parameter(description = "ID of user to deactivate", required = true)
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<Unit>> =
        userService.deactivateUser(userId).let {
            ResponseEntity.ok(ApiResponse.success(message = "User deactivated successfully"))
        }

    @Operation(
        summary = "Safe delete user",
        description = """
            Safely delete a user by marking them as deleted.
            This preserves referential integrity while making the user inaccessible.
            Municipality admins can delete any user.
            Ward admins can only delete users in their ward.
        """,
    )
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "User deleted successfully",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "User not found",
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "Insufficient permissions to delete user",
            ),
        ],
    )
    @DeleteMapping("/{userId}/delete")
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN')")
    fun deleteUser(
        @Parameter(description = "ID of user to delete", required = true)
        @PathVariable userId: String,
    ): ResponseEntity<ApiResponse<Unit>> =
        userService.safeDeleteUser(userId).let {
            ResponseEntity.ok(ApiResponse.success(message = "User deleted successfully"))
        }
}
