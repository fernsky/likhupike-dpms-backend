package np.gov.likhupikemun.dpms.family.service

import np.gov.likhupikemun.dpms.family.exception.InvalidFileTypeException
import np.gov.likhupikemun.dpms.family.repository.FamilyPhotoRepository
import np.gov.likhupikemun.dpms.family.repository.FamilyRepository
import np.gov.likhupikemun.dpms.family.service.impl.FamilyPhotoServiceImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile
import java.util.*

@ExtendWith(MockitoExtension::class)
class FamilyPhotoServiceTest {
    @Mock
    private lateinit var familyRepository: FamilyRepository

    @Mock
    private lateinit var photoRepository: FamilyPhotoRepository

    @InjectMocks
    private lateinit var photoService: FamilyPhotoServiceImpl

    @Test
    fun `should throw InvalidFileTypeException when file is not an image`() {
        // Given
        val file =
            MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".toByteArray(),
            )

        // Then
        assertThrows<InvalidFileTypeException> {
            photoService.uploadPhoto(UUID.randomUUID(), file)
        }
    }
}
