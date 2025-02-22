package np.gov.mofaga.imis.family.api.controller

import np.gov.mofaga.imis.config.TestSecurityConfig
import np.gov.mofaga.imis.family.service.FamilyPhotoService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(FamilyPhotoController::class)
@Import(TestSecurityConfig::class)
class FamilyPhotoControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var photoService: FamilyPhotoService

    private val familyId = UUID.randomUUID()
    private val photoId = UUID.randomUUID()
    private val testFile =
        MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test image content".toByteArray(),
        )

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `admin should upload photo successfully`() {
        mockMvc
            .perform(
                multipart("/api/v1/families/$familyId/photos")
                    .file(testFile)
                    .with(csrf()),
            ).andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["EDITOR"])
    fun `editor should upload photo successfully`() {
        mockMvc
            .perform(
                multipart("/api/v1/families/$familyId/photos")
                    .file(testFile)
                    .with(csrf()),
            ).andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `viewer should not be able to upload photo`() {
        mockMvc
            .perform(
                multipart("/api/v1/families/$familyId/photos")
                    .file(testFile)
                    .with(csrf()),
            ).andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `viewer should be able to view photos`() {
        mockMvc
            .perform(
                get("/api/v1/families/$familyId/photos"),
            ).andExpect(status().isOk)

        mockMvc
            .perform(
                get("/api/v1/families/$familyId/photos/$photoId"),
            ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"])
    fun `admin should delete photo successfully`() {
        mockMvc
            .perform(
                delete("/api/v1/families/$familyId/photos/$photoId")
                    .with(csrf()),
            ).andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["VIEWER"])
    fun `viewer should not be able to delete photo`() {
        mockMvc
            .perform(
                delete("/api/v1/families/$familyId/photos/$photoId")
                    .with(csrf()),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `unauthenticated user should not access any endpoint`() {
        mockMvc
            .perform(get("/api/v1/families/$familyId/photos"))
            .andExpect(status().isUnauthorized)

        mockMvc
            .perform(
                multipart("/api/v1/families/$familyId/photos")
                    .file(testFile),
            ).andExpect(status().isUnauthorized)

        mockMvc
            .perform(
                delete("/api/v1/families/$familyId/photos/$photoId"),
            ).andExpect(status().isUnauthorized)
    }
}
