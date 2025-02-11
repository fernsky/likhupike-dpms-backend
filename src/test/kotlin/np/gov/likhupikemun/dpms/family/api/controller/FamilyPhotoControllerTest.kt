package np.gov.likhupikemun.dpms.family.api.controller

import np.gov.likhupikemun.dpms.family.service.FamilyPhotoService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(FamilyPhotoController::class)
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
