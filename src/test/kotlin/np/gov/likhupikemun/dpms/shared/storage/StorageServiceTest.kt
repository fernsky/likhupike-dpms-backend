package np.gov.likhupikemun.dpms.shared.storage

import np.gov.likhupikemun.dpms.config.SharedTestConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@WebMvcTest(
    properties = ["spring.main.allow-bean-definition-overriding=true"],
)
@Import(SharedTestConfiguration::class)
@ActiveProfiles("test")
class StorageServiceTest {
    @Autowired
    private lateinit var storageService: StorageService

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
