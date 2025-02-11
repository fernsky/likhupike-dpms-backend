package np.gov.likhupikemun.dpms.family.service

import np.gov.likhupikemun.dpms.family.api.dto.response.FamilyPhotoResponse
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface FamilyPhotoService {
    fun uploadPhoto(
        familyId: UUID,
        file: MultipartFile,
    ): FamilyPhotoResponse

    fun getPhoto(photoId: UUID): ByteArray

    fun deletePhoto(photoId: UUID)

    fun getFamilyPhotos(familyId: UUID): List<FamilyPhotoResponse>
}
