package np.gov.likhupikemun.dpms.auth.api.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import np.gov.likhupikemun.dpms.auth.api.dto.UserStatus
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import java.time.LocalDateTime
import java.util.UUID

@Schema(description = "User response data")
data class UserResponse(
    @field:Schema(
        description = "Unique identifier of the user",
        example = "123e4567-e89b-12d3-a456-426614174000",
    )
    val id: UUID,
    @field:Schema(
        description = "Email address of the user",
        example = "john.doe@example.com",
    )
    val email: String,
    @field:Schema(
        description = "Full name in English",
        example = "John Doe",
    )
    val fullName: String,
    @field:Schema(
        description = "Full name in Nepali",
        example = "जोन डो",
    )
    val fullNameNepali: String,
    @field:Schema(
        description = "Ward number if applicable",
        example = "5",
    )
    val wardNumber: Int?,
    @field:Schema(
        description = "Official post in the municipality/ward",
        example = "WARD_SECRETARY",
    )
    val officePost: String?,
    @field:Schema(
        description = "User roles",
        example = "[\"WARD_ADMIN\", \"EDITOR\"]",
    )
    val roles: Set<RoleType>,
    @field:Schema(
        description = "URL to user's profile picture",
        example = "/uploads/profiles/123.jpg",
    )
    val profilePictureUrl: String?,
    @field:Schema(
        description = "User's account status",
        example = "ACTIVE",
        allowableValues = ["PENDING", "ACTIVE", "INACTIVE"],
    )
    val status: UserStatus,
    @field:Schema(
        description = "Indicates if the user is at the municipality level",
        example = "true",
    )
    val isMunicipalityLevel: Boolean,
    @field:Schema(
        description = "User creation timestamp",
        example = "2024-01-20T10:30:00",
    )
    val createdAt: LocalDateTime,
    @field:Schema(
        description = "Last update timestamp",
        example = "2024-01-20T10:30:00",
    )
    val updatedAt: LocalDateTime,
)
