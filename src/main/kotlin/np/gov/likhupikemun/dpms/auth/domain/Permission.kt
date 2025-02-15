package np.gov.likhupikemun.dpms.auth.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.common.entity.BaseEntity

@Entity
@Table(
    name = "permissions",
    indexes = [
        Index(name = "idx_permissions_name", columnList = "name"),
    ],
)
class Permission : BaseEntity() {
    @Column(nullable = false, unique = true, length = 100)
    var name: String? = null

    @Column(length = 255)
    var description: String? = null

    @ManyToMany(mappedBy = "permissions")
    var roles: MutableSet<Role> = mutableSetOf()

    // Resource type this permission applies to (e.g., USER, DOCUMENT, etc.)
    @Column(name = "resource_type", length = 50)
    var resourceType: String? = null

    // Action type (e.g., CREATE, READ, UPDATE, DELETE)
    @Column(name = "action_type", length = 20)
    @Enumerated(EnumType.STRING)
    var actionType: ActionType? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false
        other as Permission
        return name == other.name
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "Permission(name=$name, actionType=$actionType, resourceType=$resourceType)"
}

enum class ActionType {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    APPROVE,
    REJECT,
    EXPORT,
    IMPORT,
    EXECUTE,
    ;

    companion object {
        fun fromString(action: String): ActionType? = values().find { it.name.equals(action, ignoreCase = true) }
    }
}
