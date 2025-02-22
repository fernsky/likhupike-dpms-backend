package np.gov.mofaga.imis.auth.api.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User account status")
enum class UserStatus {
    @Schema(description = "Awaiting approval")
    PENDING,

    @Schema(description = "Account active")
    ACTIVE,

    @Schema(description = "Account deactivated")
    INACTIVE,
}
