package np.gov.mofaga.imis.auth.domain

import jakarta.persistence.*
import np.gov.mofaga.imis.common.entity.BaseEntity

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
@Table(
    name = "roles",
    indexes = [
        Index(name = "idx_roles_type", columnList = "role_type"),
    ],
)
class Role : BaseEntity() {
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", length = 20, nullable = false, unique = true)
    var roleType: RoleType? = null

    @Column(length = 100)
    var description: String? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "permission_id")],
    )
    var permissions: MutableSet<Permission> = mutableSetOf()

    fun getAuthority() = roleType?.getAuthority()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        other as Role
        return roleType == other.roleType
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (roleType?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "Role(roleType=$roleType)"
}
