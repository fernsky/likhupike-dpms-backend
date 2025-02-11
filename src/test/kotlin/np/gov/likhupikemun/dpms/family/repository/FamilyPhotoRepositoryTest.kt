package np.gov.likhupikemun.dpms.family.repository

import np.gov.likhupikemun.dpms.family.domain.FamilyPhoto
import np.gov.likhupikemun.dpms.family.test.fixtures.FamilyTestFixtures
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

@DataJpaTest
@ActiveProfiles("test")
class FamilyPhotoRepositoryTest {
    @Autowired
    private lateinit var familyRepository: FamilyRepository

    @Autowired
    private lateinit var photoRepository: FamilyPhotoRepository

    @Test
    fun `should find photos by family id`() {
        // Given
        val family = familyRepository.save(FamilyTestFixtures.createFamily())
        val photo =
            FamilyPhoto(
                family = family,
                fileName = "test.jpg",
                contentType = "image/jpeg",
                fileSize = 1000L,
            )
        photoRepository.save(photo)

        // When
        val photos = photoRepository.findByFamilyId(family.id!!)

        // Then
        assertEquals(1, photos.size)
        assertEquals(photo.fileName, photos.first().fileName)
    }
}
