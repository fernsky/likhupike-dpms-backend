package np.gov.likhupikemun.dpms.shared.storage

import io.minio.MinioClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile

@ExtendWith(MockitoExtension::class)
class StorageServiceTest {
    @Mock
    private lateinit var minioClient: MinioClient

    private lateinit var storageService: StorageService
    private lateinit var storageProperties: StorageProperties

    @BeforeEach
    fun setup() {
        storageProperties =
            StorageProperties(
                minio =
                    MinioProperties(
                        endpoint = "http://localhost:9000",
                        accessKey = "minioadmin",
                        secretKey = "minioadmin",
                        bucket = "test-bucket",
                    ),
            )
        storageService = StorageService(minioClient, storageProperties)
    }

    @Test
    fun `should upload file successfully`() {
        // Given
        val file =
            MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test content".toByteArray(),
            )

        // When
        storageService.uploadFile(file, "photos")

        // Then
        verify(minioClient).putObject(any())
    }
}
