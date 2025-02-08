package np.gov.likhupikemun.dpms.auth.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.shared.domain.AuditableEntity
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    private var _password: String,

    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Column(name = "full_name_np", nullable = false)
    var fullNameNepali: String,

    @Column(name = "date_of_birth")
    var dateOfBirth: LocalDate,

    var address: String,

    @Column(name = "profile_picture")
    var profilePicture: String? = null,

    @Column(name = "office_post")
    var officePost: String,

    @Column(name = "ward_number")
    var wardNumber: Int? = null,

    @Column(name = "is_municipality_level")
    var isMunicipalityLevel: Boolean = false,

    @Column(name = "is_approved")
    var isApproved: Boolean = false,

    @Column(name = "approved_by")
    var approvedBy: String? = null,

    @Column(name = "approved_at")
    var approvedAt: LocalDateTime? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()

) : AuditableEntity(), UserDetails {
    override fun getAuthorities() = roles.map { SimpleGrantedAuthority("ROLE_${it.name}") }
    override fun getPassword() = _password
    override fun getUsername() = email
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = isApproved

    // Add this if you need to modify the password
    fun setPassword(newPassword: String) {
        _password = newPassword
    }

    fun hasRole(roleType: RoleType): Boolean = 
        roles.any { it.name == roleType }

    fun isWardAdmin(): Boolean = 
        hasRole(RoleType.WARD_ADMIN)

    fun isMunicipalityAdmin(): Boolean = 
        hasRole(RoleType.MUNICIPALITY_ADMIN)
}
