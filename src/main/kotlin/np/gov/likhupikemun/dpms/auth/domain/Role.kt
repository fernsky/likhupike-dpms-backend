package np.gov.likhupikemun.dpms.auth.domain

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Entity
@Table(name = "roles")
class Role(
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    val roleType: RoleType,
    @Id
    val id: String = roleType.name,
)

@Schema(description = "Available user roles")
enum class RoleType {
    @Schema(description = "Municipality level administrator")
    MUNICIPALITY_ADMIN,

    @Schema(description = "Ward level administrator")
    WARD_ADMIN,

    @Schema(description = "Content editor")
    EDITOR,

    @Schema(description = "Read-only access")
    VIEWER,
}
