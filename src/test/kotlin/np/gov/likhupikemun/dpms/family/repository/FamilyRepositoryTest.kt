package np.gov.likhupikemun.dpms.family.repository

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class FamilyRepositoryTest
// TODO: Add test for finding families by search criteria
// {
//     @Autowired
//     private lateinit var familyRepository: FamilyRepository

//     @Test
//     fun `should find families by search criteria`() {
//         // Given
//         val family = FamilyTestFixtures.createFamily()
//         familyRepository.save(family)

//         val criteria =
//             FamilySearchCriteria(
//                 headOfFamily = "John",
//                 wardNumber = 1,
//             )

//         // When
//         val result = familyRepository.findAll(FamilySpecifications.withSearchCriteria(criteria))

//         // Then
//         assertTrue(result.isNotEmpty())
//         assertEquals(family.headOfFamily, result.first().headOfFamily)
//     }
// }
