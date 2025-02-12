package np.gov.likhupikemun.dpms.family.api.controller

import np.gov.likhupikemun.dpms.config.SharedTestConfiguration
import np.gov.likhupikemun.dpms.config.TestSecurityConfig
import np.gov.likhupikemun.dpms.family.service.FamilyPhotoService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(FamilyPhotoController::class)
@Import(TestSecurityConfig::class, SharedTestConfiguration::class) // Add this line
class FamilyPhotoControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var photoService: FamilyPhotoService

    @Test
    fun `should upload photo successfully`() {
        // Given
        val familyId = UUID.randomUUID()
        val file =
            MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".toByteArray(),
            )

        // When/Then
        mockMvc
            .perform(
                multipart("/api/v1/families/$familyId/photos")
                    .file(file),
            ).andExpect(status().isCreated)
    }
}
