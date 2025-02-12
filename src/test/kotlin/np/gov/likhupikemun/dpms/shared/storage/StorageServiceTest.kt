package np.gov.likhupikemun.dpms.shared.storage

import io.minio.*
import np.gov.likhupikemun.dpms.config.SharedTestConfiguration
import np.gov.likhupikemun.dpms.config.TestConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@WebMvcTest
@Import(TestConfig::class, SharedTestConfiguration::class)
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
        // Ensure test bucket exists
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(storageProperties.minio.bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(storageProperties.minio.bucket).build())
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

        // Verify file exists in Minio
        val exists =
            minioClient.statObject(
                StatObjectArgs
                    .builder()
                    .bucket(storageProperties.minio.bucket)
                    .`object`(result)
                    .build(),
            )
        assertTrue(exists != null)
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
        storageService.deleteFile(objectName)

        // Then
        val exists =
            try {
                minioClient.statObject(
                    StatObjectArgs
                        .builder()
                        .bucket(storageProperties.minio.bucket)
                        .`object`(objectName)
                        .build(),
                )
                true
            } catch (e: Exception) {
                false
            }
        assertTrue(!exists)
    }
}
