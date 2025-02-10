package np.gov.likhupikemun.dpms.auth.domain

import jakarta.persistence.*

enum class RoleType {
    SUPER_ADMIN,
    MUNICIPALITY_ADMIN,
    WARD_ADMIN,
    EDITOR,
    VIEWER,
    ;

    fun getAuthority() = "ROLE_${this.name}"
}

@Entity
@Table(name = "roles")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var roleType: RoleType,
    @Column(length = 50)
    var description: String? = null,
) {
    fun getAuthority() = roleType.getAuthority()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Role
        return roleType == other.roleType
    }

    override fun hashCode(): Int = roleType.hashCode()
}
