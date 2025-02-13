package np.gov.likhupikemun.dpms.family.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import np.gov.likhupikemun.dpms.config.TestSecurityConfig
import np.gov.likhupikemun.dpms.family.service.FamilyService
import np.gov.likhupikemun.dpms.family.test.fixtures.FamilyTestFixtures
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FamilyController::class)
@Import(TestSecurityConfig::class) // Add this line
@ActiveProfiles("test")
class FamilyControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var familyService: FamilyService

    @Test
    @WithMockUser(roles = ["MUNICIPALITY_ADMIN"]) // Updated to use correct role
    fun `should create family successfully`() {
        // Given
        val request = FamilyTestFixtures.createFamilyRequest()

        // When/Then
        mockMvc
            .perform(
                post("/api/v1/families")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["EDITOR"]) // Test with editor role
    fun `should create family successfully when user has editor role`() {
        val request = FamilyTestFixtures.createFamilyRequest()

        mockMvc
            .perform(
                post("/api/v1/families")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isCreated)
    }

    @Test
    @WithMockUser(roles = ["VIEWER"]) // Test with viewer role
    fun `should fail to create family when user has viewer role`() {
        val request = FamilyTestFixtures.createFamilyRequest()

        mockMvc
            .perform(
                post("/api/v1/families")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            ).andExpect(status().isForbidden)
    }

    // Add more tests for other roles and endpoints...
}
