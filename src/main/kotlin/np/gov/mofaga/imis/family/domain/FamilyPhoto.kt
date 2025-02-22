package np.gov.mofaga.imis.family.domain

import jakarta.persistence.*
import np.gov.mofaga.imis.common.entity.BaseEntity
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
@Table(
    name = "family_photos",
    indexes = [
        Index(name = "idx_family_photos_family", columnList = "family_id"),
        Index(name = "idx_family_photos_filename", columnList = "file_name"),
    ],
)
class FamilyPhoto : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family? = null

    @Column(name = "file_name", nullable = false)
    var fileName: String? = null

    @Column(name = "content_type", nullable = false)
    var contentType: String? = null

    @Column(name = "file_size", nullable = false)
    var fileSize: Long? = null

    @Column(name = "storage_path")
    var storagePath: String? = null

    @Column(name = "thumbnail_path")
    var thumbnailPath: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FamilyPhoto) return false
        if (!super.equals(other)) return false
        return fileName == other.fileName && family?.id == other.family?.id
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (family?.id?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "FamilyPhoto(id=$id, fileName=$fileName)"
}
