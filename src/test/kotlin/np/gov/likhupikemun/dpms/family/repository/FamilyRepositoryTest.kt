package np.gov.likhupikemun.dpms.family.repository

import np.gov.likhupikemun.dpms.family.api.dto.request.FamilySearchCriteria
import np.gov.likhupikemun.dpms.family.repository.specification.FamilySpecifications
import np.gov.likhupikemun.dpms.family.test.fixtures.FamilyTestFixtures
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
class FamilyRepositoryTest {
    @Autowired
    private lateinit var familyRepository: FamilyRepository

    @Test
    fun `should find families by search criteria`() {
        // Given
        val family = FamilyTestFixtures.createFamily()
        familyRepository.save(family)

        val criteria =
            FamilySearchCriteria(
                headOfFamily = "John",
                wardNumber = 1,
            )

        // When
        val result = familyRepository.findAll(FamilySpecifications.withSearchCriteria(criteria))

        // Then
        assertTrue(result.isNotEmpty())
        assertEquals(family.headOfFamily, result.first().headOfFamily)
    }
}
