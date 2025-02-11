package np.gov.likhupikemun.dpms.family.api.controller

import np.gov.likhupikemun.dpms.family.api.dto.response.FamilyPhotoResponse
import np.gov.likhupikemun.dpms.family.service.FamilyPhotoService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/v1/families/{familyId}/photos")
class FamilyPhotoController(
    private val photoService: FamilyPhotoService,
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadPhoto(
        @PathVariable familyId: UUID,
        @RequestParam("file") file: MultipartFile,
    ): FamilyPhotoResponse = photoService.uploadPhoto(familyId, file)

    @GetMapping("/{photoId}")
    fun getPhoto(
        @PathVariable photoId: UUID,
    ): ByteArray = photoService.getPhoto(photoId)

    @GetMapping
    fun getFamilyPhotos(
        @PathVariable familyId: UUID,
    ): List<FamilyPhotoResponse> = photoService.getFamilyPhotos(familyId)

    @DeleteMapping("/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePhoto(
        @PathVariable photoId: UUID,
    ) = photoService.deletePhoto(photoId)
}
