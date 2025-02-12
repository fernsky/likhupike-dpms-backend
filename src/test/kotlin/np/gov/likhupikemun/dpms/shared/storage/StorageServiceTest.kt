package np.gov.likhupikemun.dpms.shared.storage

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@WebMvcTest(
    properties = ["spring.main.allow-bean-definition-overriding=true"],
)
@ActiveProfiles("test")
class StorageServiceTest {
    @Autowired
    private lateinit var storageService: StorageService

    @Autowired
    private lateinit var minioClient: MinioClient

    @Autowired
    private lateinit var storageProperties: StorageProperties

    @BeforeEach
    fun setUp() {
        // Ensure bucket exists before each test
        if (!minioClient.bucketExists(
                BucketExistsArgs
                    .builder()
                    .bucket(storageProperties.minio.bucket)
                    .build(),
            )
        ) {
            minioClient.makeBucket(
                MakeBucketArgs
                    .builder()
                    .bucket(storageProperties.minio.bucket)
                    .build(),
            )
        }
    }

    @Test
    fun `should upload file successfully`() {
        // Given
        val file =
            MockMultipartFile(
                "test-file",
                "test.txt",
                "text/plain",
                "test content".toByteArray(),
            )
        val path = "test-path"

        // When
        val result = storageService.uploadFile(file, path)

        // Then
        assertTrue(result.startsWith("$path/"))
        assertTrue(result.endsWith(file.originalFilename!!))
    }

    @Test
    fun `should get file successfully`() {
        // Given
        val content = "test content"
        val file =
            MockMultipartFile(
                "test-file",
                "test.txt",
                "text/plain",
                content.toByteArray(),
            )
        val path = "test-path"
        val objectName = storageService.uploadFile(file, path)

        // When
        val result = storageService.getFile(objectName)

        // Then
        assertEquals(content, result.bufferedReader().use { it.readText() })
    }

    @Test
    fun `should delete file successfully`() {
        // Given
        val file =
            MockMultipartFile(
                "test-file",
                "test.txt",
                "text/plain",
                "test content".toByteArray(),
            )
        val path = "test-path"
        val objectName = storageService.uploadFile(file, path)

        // When
        storageService.deleteFile(objectName)

        // Then
        org.junit.jupiter.api.assertThrows<Exception> {
            storageService.getFile(objectName)
        }
    }
}
