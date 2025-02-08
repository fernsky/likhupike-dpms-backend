package np.gov.likhupikemun.dpms.auth.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.shared.domain.AuditableEntity

@Entity
@Table(name = "roles")
class Role(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    val name: RoleType,

    @ManyToMany(mappedBy = "roles")
    val users: MutableSet<User> = mutableSetOf()
) : AuditableEntity()

enum class RoleType {
    MUNICIPALITY_ADMIN,
    WARD_ADMIN,
    EDITOR,
    VIEWER
}
