package np.gov.likhupikemun.dpms.family.repository

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class FamilyPhotoRepositoryTest
// TODO: Add test for finding photos by family id
// {
//     @Autowired
//     private lateinit var familyRepository: FamilyRepository

//     @Autowired
//     private lateinit var photoRepository: FamilyPhotoRepository

//     @Test
//     fun `should find photos by family id`() {
//         // Given
//         val family = familyRepository.save(FamilyTestFixtures.createFamily())
//         val photo =
//             FamilyPhoto(
//                 family = family,
//                 fileName = "test.jpg",
//                 contentType = "image/jpeg",
//                 fileSize = 1000L,
//             )
//         photoRepository.save(photo)

//         // When
//         val photos = photoRepository.findByFamilyId(family.id!!)

//         // Then
//         assertEquals(1, photos.size)
//         assertEquals(photo.fileName, photos.first().fileName)
//     }
// }
