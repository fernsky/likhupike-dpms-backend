package np.gov.likhupikemun.dpms.common.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: Instant? = null

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null

    @LastModifiedBy
    @Column
    var updatedBy: String? = null

    @Version
    var version: Long? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false
        if (id == null || other.id == null) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: javaClass.hashCode()
}
