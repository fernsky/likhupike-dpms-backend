package np.gov.likhupikemun.dpms.auth.service

import np.gov.likhupikemun.dpms.auth.api.dto.UpdateUserRequest
import np.gov.likhupikemun.dpms.auth.api.dto.UserStatus
import np.gov.likhupikemun.dpms.auth.api.dto.request.CreateUserRequest
import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSearchCriteria
import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSortField
import np.gov.likhupikemun.dpms.auth.api.dto.response.UserResponse
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import np.gov.likhupikemun.dpms.auth.exception.EmailAlreadyExistsException
import np.gov.likhupikemun.dpms.auth.exception.UserApprovalException
import np.gov.likhupikemun.dpms.auth.exception.UserDeletionException
import np.gov.likhupikemun.dpms.auth.exception.UserNotFoundException
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.RoleRepository
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.UserRepository
import np.gov.likhupikemun.dpms.auth.infrastructure.repository.specifications.UserSpecifications
import np.gov.likhupikemun.dpms.shared.exception.*
import np.gov.likhupikemun.dpms.shared.exception.ForbiddenException
import np.gov.likhupikemun.dpms.shared.service.FileService
import np.gov.likhupikemun.dpms.shared.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileService: FileService,
    private val securityService: SecurityService,
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        val currentUser = securityService.getCurrentUser()
        validateUserCreation(currentUser, request)

        if (userRepository.existsByEmail(request.email)) {
            throw EmailAlreadyExistsException(request.email)
        }

        return createUserFromRequest(request).let {
            userRepository.save(it).toResponse()
        }
    }

    @Transactional
    fun approveUser(userId: String): UserResponse {
        logger.debug("Attempting to approve user with ID: {}", userId)

        val currentUser = securityService.getCurrentUser()
        logger.debug(
            "Current user: {} (Ward: {}, MunicipalityAdmin: {})",
            currentUser.email,
            currentUser.wardNumber,
            currentUser.isMunicipalityAdmin(),
        )

        val user = findUserById(userId)
        logger.debug(
            "Found user to approve: {} (Ward: {}, MunicipalityLevel: {})",
            user.email,
            user.wardNumber,
            user.isMunicipalityLevel,
        )

        validateUserApproval(currentUser, user)
        logger.debug("User approval validation passed")

        return user
            .apply {
                isApproved = true
                approvedBy = currentUser.id
                approvedAt = LocalDateTime.now()
                logger.debug("Setting approval details - approvedBy: {}, approvedAt: {}", approvedBy, approvedAt)
            }.let {
                val savedUser = userRepository.save(it)
                logger.debug("User successfully approved and saved")
                savedUser.toResponse()
            }
    }

    @Transactional(readOnly = true)
    fun getPendingUsers(
        wardNumber: Int?,
        pageable: Pageable,
    ): Page<UserResponse> =
        userRepository
            .findByIsApprovedFalseAndWardNumberOrWardNumberIsNull(wardNumber, pageable)
            .map { it.toResponse() }

    @Transactional(readOnly = true)
    @Cacheable(value = ["users"], key = "#criteria.toString()")
    fun searchUsers(criteria: UserSearchCriteria): Page<UserResponse> {
        val currentUser = securityService.getCurrentUser()
        validateSearchPermissions(currentUser, criteria)

        val sortOrder =
            when (criteria.sortBy) {
                UserSortField.CREATED_AT -> Sort.by(criteria.sortDirection, "createdAt")
                UserSortField.FULL_NAME -> Sort.by(criteria.sortDirection, "fullName")
                UserSortField.FULL_NAME_NEPALI -> Sort.by(criteria.sortDirection, "fullNameNepali")
                UserSortField.WARD_NUMBER -> Sort.by(criteria.sortDirection, "wardNumber")
                UserSortField.OFFICE_POST -> Sort.by(criteria.sortDirection, "officePost")
                UserSortField.EMAIL -> Sort.by(criteria.sortDirection, "email")
                UserSortField.APPROVAL_STATUS -> Sort.by(criteria.sortDirection, "isApproved")
            }

        val pageable = PageRequest.of(criteria.page, criteria.pageSize, sortOrder)
        val specification = UserSpecifications.fromCriteria(criteria)

        return userRepository.findAll(specification, pageable).map { it.toResponse() }
    }

    @Transactional
    fun updateUser(
        userId: String,
        request: UpdateUserRequest,
    ): UserResponse {
        val currentUser = securityService.getCurrentUser()
        val user =
            userRepository
                .findById(userId)
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
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { UserNotFoundException(userId) }

        validateUserDeactivation(currentUser, user)

        user.isApproved = false
        userRepository.save(user)
    }

    @Transactional
    @CacheEvict(value = ["user"], key = "#userId")
    fun safeDeleteUser(userId: String) {
        val currentUser = securityService.getCurrentUser()
        val user = findUserById(userId)

        validateUserDeletion(currentUser, user)

        user.apply {
            isDeleted = true
            deletedAt = LocalDateTime.now()
            deletedBy = currentUser.id
            email = "$email.deleted.${System.currentTimeMillis()}" // Ensure email uniqueness
            isApproved = false
        }

        userRepository.save(user)
    }

    private fun validateUserCreation(
        currentUser: User,
        request: CreateUserRequest,
    ) {
        when {
            request.isMunicipalityLevel && !currentUser.isMunicipalityAdmin() ->
                throw ForbiddenException("Only municipality admins can create municipality-level users")
            request.wardNumber != null && !currentUser.isWardAdmin() && !currentUser.isMunicipalityAdmin() ->
                throw ForbiddenException("Only ward admins can create ward-level users")
            request.wardNumber != null && currentUser.isWardAdmin() && request.wardNumber != currentUser.wardNumber ->
                throw ForbiddenException("Ward admin can only create users for their own ward")
        }
    }

    private fun validateUserApproval(
        currentUser: User,
        userToApprove: User,
    ) {
        logger.debug(
            "Validating user approval - Approver: {} (Ward: {}), User: {} (Ward: {})",
            currentUser.email,
            currentUser.wardNumber,
            userToApprove.email,
            userToApprove.wardNumber,
        )

        when {
            userToApprove.isApproved -> {
                logger.debug("Validation failed: User is already approved")
                throw UserApprovalException("User is already approved")
            }

            userToApprove.isMunicipalityLevel && !currentUser.isMunicipalityAdmin() -> {
                logger.debug("Validation failed: Non-municipality admin attempting to approve municipality-level user")
                throw ForbiddenException(
                    message = "Only municipality admins can approve municipality-level users",
                    details =
                        mapOf(
                            "userType" to "MUNICIPALITY_LEVEL",
                            "requiredRole" to "MUNICIPALITY_ADMIN",
                        ),
                )
            }

            !currentUser.isMunicipalityAdmin() && !currentUser.isWardAdmin() -> {
                logger.debug("Validation failed: Non-admin user attempting to approve")
                throw ForbiddenException(
                    message = "Only admins can approve users",
                    details =
                        mapOf(
                            "requiredRole" to "MUNICIPALITY_ADMIN or WARD_ADMIN",
                        ),
                )
            }

            currentUser.isWardAdmin() && userToApprove.wardNumber != currentUser.wardNumber -> {
                logger.debug("Validation failed: Ward admin attempting to approve user from different ward")
                throw ForbiddenException(
                    message = "Ward admin can only approve users in their ward",
                    details =
                        mapOf(
                            "adminWard" to currentUser.wardNumber!!,
                            "userWard" to (userToApprove.wardNumber ?: "none"),
                        ),
                )
            }
        }

        logger.debug("User approval validation successful")
    }

    private fun validateRoleAssignment(
        admin: User,
        user: User,
        newRoles: Set<RoleType>,
    ) {
        val isMunicipalityAdmin = admin.isMunicipalityAdmin()
        val isWardAdmin = admin.isWardAdmin()

        when {
            !isMunicipalityAdmin && !isWardAdmin ->
                throw ForbiddenException("Only admins can assign roles")

            newRoles.contains(RoleType.MUNICIPALITY_ADMIN) && !isMunicipalityAdmin ->
                throw ForbiddenException("Only municipality admins can assign municipality admin role")

            newRoles.contains(RoleType.WARD_ADMIN) &&
                !isMunicipalityAdmin &&
                (admin.wardNumber != user.wardNumber) ->
                throw ForbiddenException("Ward admin can only assign roles in their ward")
        }

        val persistentRoles = roleRepository.findByNameIn(newRoles)
        user.roles = persistentRoles.toMutableSet()
    }

    private fun validateUserDeactivation(
        admin: User,
        user: User,
    ) {
        when {
            user.isMunicipalityAdmin() && !admin.isMunicipalityAdmin() ->
                throw ForbiddenException("Only municipality admins can deactivate municipality admins")

            user.isWardAdmin() && !admin.isMunicipalityAdmin() ->
                throw ForbiddenException("Only municipality admins can deactivate ward admins")

            admin.isWardAdmin() && admin.wardNumber != user.wardNumber ->
                throw ForbiddenException("Ward admin can only deactivate users in their ward")
        }
    }

    private fun validateUserDeletion(
        admin: User,
        user: User,
    ) {
        when {
            user.isDeleted ->
                throw UserDeletionException("User is already deleted")
            user.isMunicipalityLevel && !admin.isMunicipalityAdmin() ->
                throw ForbiddenException(
                    message = "Only municipality admins can delete municipality-level users",
                    details =
                        mapOf(
                            "userId" to (user.id ?: ""),
                            "requiredRole" to "MUNICIPALITY_ADMIN",
                            "userType" to "MUNICIPALITY_LEVEL",
                        ),
                )
            user.isMunicipalityAdmin() && !admin.isMunicipalityAdmin() ->
                throw ForbiddenException(
                    message = "Only municipality admins can delete municipality admins",
                    details =
                        mapOf(
                            "userId" to user.id!!,
                            "requiredRole" to "MUNICIPALITY_ADMIN",
                        ),
                )
            admin.isWardAdmin() && admin.wardNumber != user.wardNumber ->
                throw ForbiddenException(
                    message = "Ward admin can only delete users in their ward",
                    details =
                        mapOf(
                            "userId" to user.id!!,
                            "requestedWard" to (user.wardNumber?.toString() ?: "none"),
                            "adminWard" to admin.wardNumber.toString(), // Convert Int to String
                        ),
                )
        }
    }

    private fun validateUserUpdate(
        admin: User,
        user: User,
    ) {
        when {
            user.isMunicipalityAdmin() && !admin.isMunicipalityAdmin() ->
                throw ForbiddenException("Only municipality admins can update municipality admin users")
            admin.isWardAdmin() && admin.wardNumber != user.wardNumber ->
                throw ForbiddenException("Ward admin can only update users in their ward")
        }
    }

    private fun validateSearchPermissions(
        currentUser: User,
        criteria: UserSearchCriteria,
    ) {
        when {
            !currentUser.isMunicipalityAdmin() && criteria.isMunicipalityLevel == true ->
                throw ForbiddenException("Only municipality admins can search municipality-level users")

            currentUser.isWardAdmin() &&
                criteria.wardNumberFrom != null &&
                criteria.wardNumberFrom != currentUser.wardNumber ->
                throw ForbiddenException("Ward admin can only search users in their ward")

            currentUser.isWardAdmin() &&
                criteria.wardNumberTo != null &&
                criteria.wardNumberTo != currentUser.wardNumber ->
                throw ForbiddenException("Ward admin can only search users in their ward")
        }
    }

    private fun createUserFromRequest(request: CreateUserRequest): User {
        val user =
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                fullName = request.fullName,
                fullNameNepali = request.fullNameNepali,
                dateOfBirth = LocalDate.parse(request.dateOfBirth),
                address = request.address,
                officePost = request.officePost,
                wardNumber = request.wardNumber,
                isMunicipalityLevel = request.isMunicipalityLevel,
            )

        request.profilePicture?.let {
            user.profilePicture = fileService.storeFile(it)
        }

        val domainRoles =
            if (request.roles.isEmpty()) {
                setOf(RoleType.VIEWER)
            } else {
                request.roles.map { convertToEntityRole(it) }.toSet()
            }

        val persistentRoles = roleRepository.findByNameIn(domainRoles)
        user.roles = persistentRoles.toMutableSet()
        return user
    }

    private fun findUserById(userId: String): User =
        userRepository
            .findById(userId)
            .orElseThrow { UserNotFoundException(userId) }

    private fun User.toResponse(): UserResponse =
        UserResponse(
            id = id!!,
            email = email,
            fullName = fullName,
            fullNameNepali = fullNameNepali,
            wardNumber = wardNumber,
            officePost = officePost,
            roles = roles.map { it.roleType }.toSet(),
            status = if (isApproved) UserStatus.ACTIVE else UserStatus.PENDING,
            profilePictureUrl = profilePicture?.let { "/uploads/profiles/$it" },
            isMunicipalityLevel = isMunicipalityLevel, // Add this field
            createdAt = createdAt ?: LocalDateTime.now(),
            updatedAt = updatedAt ?: LocalDateTime.now(),
        )

    private fun updateUserRoles(
        user: User,
        newDtoRoles: Set<RoleType>,
    ) {
        val domainRoleTypes = newDtoRoles.map { convertToEntityRole(it) }
        val persistentRoles = roleRepository.findByNameIn(domainRoleTypes)
        user.roles = persistentRoles.toMutableSet()
    }

    private fun convertToEntityRole(dtoRole: RoleType): RoleType = RoleType.valueOf(dtoRole.name)

    private fun convertToDtoRole(entityRole: RoleType): RoleType = RoleType.valueOf(entityRole.name)
}
