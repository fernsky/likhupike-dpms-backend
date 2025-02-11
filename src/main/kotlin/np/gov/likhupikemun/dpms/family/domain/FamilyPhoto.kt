package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "family_photos")
class FamilyPhoto(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family,
    @Column(nullable = false)
    var fileName: String,
    @Column(nullable = false)
    var contentType: String,
    @Column(nullable = false)
    var fileSize: Long,
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
)
