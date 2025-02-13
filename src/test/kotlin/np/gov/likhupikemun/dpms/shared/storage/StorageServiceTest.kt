package np.gov.likhupikemun.dpms.shared.storage

import io.minio.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@WebMvcTest(StorageService::class)
@Import(StorageTestConfig::class)
@EnableAutoConfiguration(exclude = [RedisAutoConfiguration::class])
@ContextConfiguration(classes = [StorageTestConfig::class])
class StorageServiceTest {
    @Autowired
    private lateinit var storageService: StorageService

    @Autowired
    private lateinit var minioClient: MinioClient

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
        whenever(minioClient.putObject(any())).thenReturn(null)
        val result = storageService.uploadFile(file, path)

        // Then
        assertTrue(result.startsWith("$path/"))
        assertTrue(result.endsWith(file.originalFilename!!))
        verify(minioClient).putObject(any<PutObjectArgs>())
    }

    @Test
    fun `should get file successfully`() {
        // Given
        val content = "test content"
        val path = "test-path/test.txt"

        // When
        whenever(minioClient.getObject(any())).thenAnswer {
            ByteArrayInputStream(content.toByteArray())
        }
        val result = storageService.getFile(path)

        // Then
        assertEquals(content, result.bufferedReader().use { it.readText() })
        verify(minioClient).getObject(any<GetObjectArgs>())
    }

    @Test
    fun `should delete file successfully`() {
        // Given
        val path = "test-path/test.txt"
        doNothing().whenever(minioClient).removeObject(any())

        // When
        storageService.deleteFile(path)

        // Then
        verify(minioClient).removeObject(any<RemoveObjectArgs>())
    }
}
