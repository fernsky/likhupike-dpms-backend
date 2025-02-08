package np.gov.likhupikemun.dpms.auth.api

import np.gov.likhupikemun.dpms.auth.api.dto.*
import np.gov.likhupikemun.dpms.auth.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN')")
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.createUser(request))

    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN')")
    fun approveUser(@PathVariable userId: String): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.approveUser(userId))

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MUNICIPALITY_ADMIN', 'WARD_ADMIN')")
    fun getPendingUsers(
        @RequestParam(required = false) wardNumber: Int?,
        pageable: Pageable
    ): ResponseEntity<Page<UserResponse>> =
        ResponseEntity.ok(userService.getPendingUsers(wardNumber, pageable))

    // Additional endpoints...
}
