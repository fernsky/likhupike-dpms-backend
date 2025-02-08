package np.gov.likhupikemun.dpms.auth.domain

import jakarta.persistence.*

@Entity
@Table(name = "permissions")
class Permission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String
)
