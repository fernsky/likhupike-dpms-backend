package np.gov.likhupikemun.dpms.auth.api.dto

import np.gov.likhupikemun.dpms.auth.domain.RoleType
import jakarta.validation.constraints.*
import org.springframework.web.multipart.MultipartFile

data class CreateUserRequest(
    @field:Email
    val email: String,
    @field:Size(min = 8)
    val password: String,
    @field:NotBlank
    val fullName: String,
    @field:NotBlank
    val fullNameNepali: String,
    @field:NotNull
    val dateOfBirth: String,
    @field:NotBlank
    val address: String,
    val profilePicture: MultipartFile?,
    @field:NotBlank
    val officePost: String,
    val wardNumber: Int?,
    val isMunicipalityLevel: Boolean = false,
    val roles: Set<RoleType> = setOf()
)

data class UpdateUserRequest(
    val fullName: String?,
    val fullNameNepali: String?,
    val address: String?,
    val profilePicture: MultipartFile?,
    val officePost: String?,
    val roles: Set<RoleType>?
)

data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val fullNameNepali: String,
    val dateOfBirth: String,
    val address: String,
    val profilePicture: String?,
    val officePost: String,
    val wardNumber: Int?,
    val isMunicipalityLevel: Boolean,
    val isApproved: Boolean,
    val roles: Set<RoleType>,
    val createdAt: String,
    val updatedAt: String?
)
