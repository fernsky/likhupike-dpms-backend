package np.gov.likhupikemun.dpms.shared.storage

import io.minio.*
import okhttp3.Headers
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
import java.io.FilterInputStream
import java.io.InputStream
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
        val testStream = TestInputStream(ByteArrayInputStream(content.toByteArray()))

        val mockResponse =
            GetObjectResponse(
                Headers.Builder().build(),
                "test-bucket",
                "test-region",
                path,
                testStream,
            )

        // When
        whenever(minioClient.getObject(any())).thenReturn(mockResponse)
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

    private class TestInputStream(
        inputStream: InputStream,
    ) : FilterInputStream(inputStream) {
        override fun available(): Int = `in`.available()

        override fun read(): Int = `in`.read()

        override fun read(b: ByteArray): Int = `in`.read(b)

        override fun read(
            b: ByteArray,
            off: Int,
            len: Int,
        ): Int = `in`.read(b, off, len)

        override fun skip(n: Long): Long = `in`.skip(n)

        override fun markSupported(): Boolean = `in`.markSupported()

        override fun mark(readlimit: Int) = `in`.mark(readlimit)

        override fun reset() = `in`.reset()

        override fun close() = `in`.close()
    }
}
