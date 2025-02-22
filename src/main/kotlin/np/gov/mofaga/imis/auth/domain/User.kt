package np.gov.mofaga.imis.auth.domain

import jakarta.persistence.*
import np.gov.mofaga.imis.common.entity.BaseEntity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_users_email", columnList = "email"),
        Index(name = "idx_users_full_name", columnList = "full_name"),
    ],
)
class User :
    BaseEntity(),
    UserDetails {
    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(nullable = false)
    private var password: String? = null

    @Column(name = "full_name", nullable = false)
    var fullName: String? = null

    @Column(name = "full_name_np", nullable = false)
    var fullNameNepali: String? = null

    @Column(name = "date_of_birth")
    var dateOfBirth: LocalDate? = null

    var address: String? = null

    @Column(name = "profile_picture")
    var profilePicture: String? = null

    @Column(name = "office_post")
    var officePost: String? = null

    @Column(name = "ward_number")
    var wardNumber: Int? = null

    @Column(name = "is_municipality_level")
    var isMunicipalityLevel: Boolean = false

    @Column(name = "is_approved")
    var isApproved: Boolean = false

    @Column(name = "approved_by")
    var approvedBy: String? = null

    @Column(name = "approved_at")
    var approvedAt: LocalDateTime? = null

    @Column(name = "is_deleted")
    var isDeleted: Boolean = false

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null

    @Column(name = "deleted_by")
    var deletedBy: String? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")],
    )
    var roles: MutableSet<Role> = mutableSetOf()

    // UserDetails implementation
    override fun getAuthorities() = roles.map { SimpleGrantedAuthority(it.getAuthority()) }

    override fun getPassword() = password

    override fun getUsername() = email

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = isApproved && !isDeleted

    // Password management
    fun setPassword(newPassword: String) {
        password = newPassword
    }

    // Role checks
    fun hasRole(roleType: RoleType) = roles.any { it.roleType == roleType }

    fun isSuperAdmin() = hasRole(RoleType.SUPER_ADMIN)

    fun isMunicipalityAdmin() = hasRole(RoleType.MUNICIPALITY_ADMIN)

    fun isWardAdmin() = hasRole(RoleType.WARD_ADMIN)
}
