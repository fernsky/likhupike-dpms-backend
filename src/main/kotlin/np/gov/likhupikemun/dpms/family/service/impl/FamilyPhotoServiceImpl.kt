package np.gov.likhupikemun.dpms.family.service.impl

import np.gov.likhupikemun.dpms.family.api.dto.response.FamilyPhotoResponse
import np.gov.likhupikemun.dpms.family.api.mapper.FamilyMapper
import np.gov.likhupikemun.dpms.family.domain.Family
import np.gov.likhupikemun.dpms.family.domain.FamilyPhoto
import np.gov.likhupikemun.dpms.family.exception.FamilyNotFoundException
import np.gov.likhupikemun.dpms.family.exception.FamilyPhotoNotFoundException
import np.gov.likhupikemun.dpms.family.exception.InvalidFileTypeException
import np.gov.likhupikemun.dpms.family.repository.FamilyPhotoRepository
import np.gov.likhupikemun.dpms.family.repository.FamilyRepository
import np.gov.likhupikemun.dpms.family.service.FamilyPhotoService
import np.gov.likhupikemun.dpms.shared.storage.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class FamilyPhotoServiceImpl(
    private val familyPhotoRepository: FamilyPhotoRepository,
    private val familyRepository: FamilyRepository,
    private val storageService: StorageService,
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
            createFamilyPhoto(
                family = family,
                fileName = file.originalFilename ?: "unnamed.jpg",
                contentType = file.contentType ?: "image/jpeg",
                fileSize = file.size,
            )

        return FamilyMapper.toPhotoResponse(familyPhotoRepository.save(photo))
    }

    @Transactional(readOnly = true)
    override fun getPhoto(photoId: UUID): ByteArray {
        val photo =
            familyPhotoRepository
                .findById(photoId)
                .orElseThrow { FamilyPhotoNotFoundException() }

        val inputStream = storageService.getFile(photo.fileName ?: throw FamilyPhotoNotFoundException())
        return inputStream.use { it.readAllBytes() }
    }

    @Transactional
    override fun deletePhoto(photoId: UUID) {
        if (!familyPhotoRepository.existsById(photoId)) {
            throw FamilyPhotoNotFoundException()
        }
        familyPhotoRepository.deleteById(photoId)
    }

    @Transactional(readOnly = true)
    override fun getFamilyPhotos(familyId: UUID): List<FamilyPhotoResponse> =
        familyPhotoRepository.findByFamilyId(familyId).map(FamilyMapper::toPhotoResponse)

    private fun validateImageFile(file: MultipartFile) {
        if (file.contentType?.startsWith("image/") != true) {
            throw InvalidFileTypeException()
        }
    }

    private fun createFamilyPhoto(
        family: Family,
        fileName: String,
        contentType: String,
        fileSize: Long,
    ): FamilyPhoto =
        FamilyPhoto().apply {
            this.family = family
            this.fileName = fileName
            this.contentType = contentType
            this.fileSize = fileSize
        }
}
