package np.gov.likhupikemun.dpms.family.api.dto.response

import java.time.LocalDateTime
import java.util.UUID

data class FamilyPhotoResponse(
    val id: UUID,
    val familyId: UUID,
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val createdAt: LocalDateTime?, // Made nullable
    val thumbnailUrl: String? = null,
    val url: String? = null,
)
