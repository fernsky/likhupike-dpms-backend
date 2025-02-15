package np.gov.likhupikemun.dpms.family.service

import np.gov.likhupikemun.dpms.family.exception.FamilyNotFoundException
import np.gov.likhupikemun.dpms.family.exception.FamilyPhotoNotFoundException
import np.gov.likhupikemun.dpms.family.exception.InvalidFileTypeException
import np.gov.likhupikemun.dpms.family.repository.FamilyPhotoRepository
import np.gov.likhupikemun.dpms.family.repository.FamilyRepository
import np.gov.likhupikemun.dpms.family.service.impl.FamilyPhotoServiceImpl
import np.gov.likhupikemun.dpms.family.test.fixtures.FamilyTestFixtures
import np.gov.likhupikemun.dpms.shared.storage.StorageService
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.mock.web.MockMultipartFile
import java.util.*

@ExtendWith(MockitoExtension::class)
class FamilyPhotoServiceTest {
    @Mock
    private lateinit var familyPhotoRepository: FamilyPhotoRepository

    @Mock
    private lateinit var familyRepository: FamilyRepository

    @Mock
    private lateinit var storageService: StorageService

    private lateinit var photoService: FamilyPhotoServiceImpl

    @BeforeEach
    fun setup() {
        photoService = FamilyPhotoServiceImpl(familyPhotoRepository, familyRepository, storageService)
    }

    // TODO: Fix this test
    // @Test
    // fun `should upload photo successfully`() {
    //     // Arrange
    //     val family = FamilyTestFixtures.createFamily()
    //     val file =
    //         MockMultipartFile(
    //             "file",
    //             "test.jpg",
    //             "image/jpeg",
    //             "test image content".toByteArray(),
    //         )
    //     val storedFilePath = "photos/families/${family.id}/test.jpg"

    //     whenever(familyRepository.findById(any())).thenReturn(Optional.of(family))
    //     whenever(familyPhotoRepository.save(any())).thenAnswer { invocation ->
    //         val photoToSave = invocation.arguments[0] as FamilyPhoto
    //         FamilyPhoto().apply {
    //             id = UUID.randomUUID()
    //             this.family = photoToSave.family
    //             fileName = storedFilePath
    //             contentType = photoToSave.contentType
    //             fileSize = photoToSave.fileSize
    //         }
    //     }

    //     // Act
    //     val result = photoService.uploadPhoto(family.id!!, file)

    //     // Assert
    //     assertEquals(storedFilePath, result.fileName)
    //     assertEquals("image/jpeg", result.contentType)
    //     verify(familyPhotoRepository).save(any())
    // }

    @Test
    fun `should throw InvalidFileTypeException when file is not an image`() {
        // Arrange
        val family = FamilyTestFixtures.createFamily()
        val file =
            MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".toByteArray(),
            )

        // Act & Assert
        assertThrows(InvalidFileTypeException::class.java) {
            photoService.uploadPhoto(family.id!!, file)
        }
    }

    @Test
    fun `should throw FamilyNotFoundException when family not found`() {
        // Arrange
        val file =
            MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".toByteArray(),
            )

        whenever(familyRepository.findById(any())).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows(FamilyNotFoundException::class.java) {
            photoService.uploadPhoto(UUID.randomUUID(), file)
        }
    }

    @Test
    fun `should delete photo successfully`() {
        // Arrange
        val photoId = UUID.randomUUID()
        whenever(familyPhotoRepository.existsById(photoId)).thenReturn(true)

        // Act
        photoService.deletePhoto(photoId)

        // Assert
        verify(familyPhotoRepository).deleteById(photoId)
    }

    @Test
    fun `should throw FamilyPhotoNotFoundException when deleting non-existent photo`() {
        // Arrange
        val photoId = UUID.randomUUID()
        whenever(familyPhotoRepository.existsById(photoId)).thenReturn(false)

        // Act & Assert
        assertThrows(FamilyPhotoNotFoundException::class.java) {
            photoService.deletePhoto(photoId)
        }
    }
}
