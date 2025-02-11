package np.gov.likhupikemun.dpms.family.service.impl

import np.gov.likhupikemun.dpms.family.api.dto.response.FamilyPhotoResponse
import np.gov.likhupikemun.dpms.family.api.mapper.FamilyMapper
import np.gov.likhupikemun.dpms.family.domain.FamilyPhoto
import np.gov.likhupikemun.dpms.family.exception.FamilyNotFoundException
import np.gov.likhupikemun.dpms.family.exception.FamilyPhotoNotFoundException
import np.gov.likhupikemun.dpms.family.exception.InvalidFileTypeException
import np.gov.likhupikemun.dpms.family.repository.FamilyPhotoRepository
import np.gov.likhupikemun.dpms.family.repository.FamilyRepository
import np.gov.likhupikemun.dpms.family.service.FamilyPhotoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class FamilyPhotoServiceImpl(
    private val familyRepository: FamilyRepository,
    private val photoRepository: FamilyPhotoRepository,
) : FamilyPhotoService {
    @Transactional
    override fun uploadPhoto(
        familyId: UUID,
        file: MultipartFile,
    ): FamilyPhotoResponse {
        validateImageFile(file)
        val family =
            familyRepository
                .findById(familyId)
                .orElseThrow { FamilyNotFoundException() }

        val photo =
            FamilyPhoto(
                family = family,
                fileName = file.originalFilename ?: "unnamed.jpg",
                contentType = file.contentType ?: "image/jpeg",
                fileSize = file.size,
            )

        // TODO: Implement actual file storage logic
        return FamilyMapper.toPhotoResponse(photoRepository.save(photo))
    }

    @Transactional(readOnly = true)
    override fun getPhoto(photoId: UUID): ByteArray {
        val photo =
            photoRepository
                .findById(photoId)
                .orElseThrow { FamilyPhotoNotFoundException() }

        // TODO: Implement actual file retrieval logic
        return ByteArray(0)
    }

    @Transactional
    override fun deletePhoto(photoId: UUID) {
        if (!photoRepository.existsById(photoId)) {
            throw FamilyPhotoNotFoundException()
        }
        photoRepository.deleteById(photoId)
        // TODO: Implement actual file deletion logic
    }

    @Transactional(readOnly = true)
    override fun getFamilyPhotos(familyId: UUID): List<FamilyPhotoResponse> =
        photoRepository.findByFamilyId(familyId).map { FamilyMapper.toPhotoResponse(it) }

    private fun validateImageFile(file: MultipartFile) {
        if (!file.contentType?.startsWith("image/") == true) {
            throw InvalidFileTypeException()
        }
    }
}
