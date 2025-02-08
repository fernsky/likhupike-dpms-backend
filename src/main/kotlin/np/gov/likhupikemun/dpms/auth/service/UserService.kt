package np.gov.likhupikemun.dpms.auth.service

import np.gov.likhupikemun.dpms.auth.api.dto.*
import np.gov.likhupikemun.dpms.auth.domain.*
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.UserRepository
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.RoleRepository
import np.gov.likhupikemun.dpms.shared.exception.*
import np.gov.likhupikemun.dpms.shared.service.FileService
import np.gov.likhupikemun.dpms.shared.service.SecurityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileService: FileService,
    private val securityService: SecurityService
) {
    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        val currentUser = securityService.getCurrentUser()
        validateUserCreation(currentUser, request)

        val user = createUserFromRequest(request)
        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun approveUser(userId: String): UserResponse {
        val currentUser = securityService.getCurrentUser()
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

        validateUserApproval(currentUser, user)

        user.isApproved = true
        user.approvedBy = currentUser.id
        user.approvedAt = LocalDateTime.now()

        return userRepository.save(user).toResponse()
    }

    @Transactional(readOnly = true)
    fun getPendingUsers(wardNumber: Int?, pageable: Pageable): Page<UserResponse> =
        userRepository.findPendingUsers(wardNumber, pageable)
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    fun searchUsers(
        wardNumber: Int?,
        searchTerm: String?,
        pageable: Pageable
    ): Page<UserResponse> =
        userRepository.searchUsers(wardNumber, searchTerm, pageable)
            .map { it.toResponse() }

    @Transactional
    fun updateUser(userId: String, request: UpdateUserRequest): UserResponse {
        val currentUser = securityService.getCurrentUser()
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

        validateUserUpdate(currentUser, user)

        with(user) {
            request.fullName?.let { fullName = it }
            request.fullNameNepali?.let { fullNameNepali = it }
            request.address?.let { address = it }
            request.officePost?.let { officePost = it }
        }

        request.profilePicture?.let { file ->
            val filename = fileService.storeFile(file)
            user.profilePicture?.let { oldFile -> fileService.deleteFile(oldFile) }
            user.profilePicture = filename
        }

        request.roles?.let { newRoles ->
            validateRoleAssignment(currentUser, user, newRoles)
            user.roles = roleRepository.findByNameIn(newRoles).toMutableSet()
        }

        return userRepository.save(user).toResponse()
    }

    @Transactional
    @CacheEvict(value = ["user"], key = "#userId")
    fun deactivateUser(userId: String) {
        val currentUser = securityService.getCurrentUser()
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

        validateUserDeactivation(currentUser, user)

        user.isApproved = false
        userRepository.save(user)
    }

    private fun validateUserCreation(currentUser: User, request: CreateUserRequest) {
        when {
            request.isMunicipalityLevel && !currentUser.isMunicipalityAdmin() ->
                throw UnauthorizedException("Only municipality admins can create municipality-level users")
            request.wardNumber != null && !currentUser.isWardAdmin() && !currentUser.isMunicipalityAdmin() ->
                throw UnauthorizedException("Only ward admins can create ward-level users")
            request.wardNumber != null && currentUser.isWardAdmin() && request.wardNumber != currentUser.wardNumber ->
                throw UnauthorizedException("Ward admin can only create users for their own ward")
        }
    }

    private fun validateUserApproval(approver: User, userToApprove: User) {
        when {
            userToApprove.isApproved ->
                throw InvalidOperationException("User is already approved")
            userToApprove.isMunicipalityLevel && !approver.isMunicipalityAdmin() ->
                throw UnauthorizedException("Only municipality admins can approve municipality-level users")
            !userToApprove.isMunicipalityLevel && !approver.isMunicipalityAdmin() && 
                (!approver.isWardAdmin() || approver.wardNumber != userToApprove.wardNumber) ->
                throw UnauthorizedException("Ward admin can only approve users from their ward")
        }
    }

    private fun validateRoleAssignment(admin: User, user: User, newRoles: Set<RoleType>) {
        val isMunicipalityAdmin = admin.isMunicipalityAdmin()
        val isWardAdmin = admin.isWardAdmin()

        when {
            !isMunicipalityAdmin && !isWardAdmin ->
                throw UnauthorizedException("Only admins can assign roles")
            
            newRoles.contains(RoleType.MUNICIPALITY_ADMIN) && !isMunicipalityAdmin ->
                throw UnauthorizedException("Only municipality admins can assign municipality admin role")
            
            newRoles.contains(RoleType.WARD_ADMIN) && !isMunicipalityAdmin && 
                (admin.wardNumber != user.wardNumber) ->
                throw UnauthorizedException("Ward admin can only assign roles in their ward")
        }
    }

    private fun validateUserDeactivation(admin: User, user: User) {
        when {
            user.isMunicipalityAdmin() && !admin.isMunicipalityAdmin() ->
                throw UnauthorizedException("Only municipality admins can deactivate municipality admins")
            
            user.isWardAdmin() && !admin.isMunicipalityAdmin() ->
                throw UnauthorizedException("Only municipality admins can deactivate ward admins")
            
            admin.isWardAdmin() && admin.wardNumber != user.wardNumber ->
                throw UnauthorizedException("Ward admin can only deactivate users in their ward")
        }
    }

    private fun validateUserUpdate(admin: User, user: User) {
        when {
            user.isMunicipalityAdmin() && !admin.isMunicipalityAdmin() ->
                throw UnauthorizedException("Only municipality admins can update municipality admin users")
            admin.isWardAdmin() && admin.wardNumber != user.wardNumber ->
                throw UnauthorizedException("Ward admin can only update users in their ward")
        }
    }

    private fun createUserFromRequest(request: CreateUserRequest): User {
        val user = User(
            email = request.email,
            _password = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            fullNameNepali = request.fullNameNepali,
            dateOfBirth = LocalDate.parse(request.dateOfBirth),
            address = request.address,
            officePost = request.officePost,
            wardNumber = request.wardNumber,
            isMunicipalityLevel = request.isMunicipalityLevel
        )

        request.profilePicture?.let {
            user.profilePicture = fileService.storeFile(it)
        }

        val roles = if (request.roles.isEmpty()) {
            setOf(RoleType.VIEWER)
        } else {
            request.roles
        }

        user.roles.addAll(roleRepository.findByNameIn(roles))
        return user
    }
}
