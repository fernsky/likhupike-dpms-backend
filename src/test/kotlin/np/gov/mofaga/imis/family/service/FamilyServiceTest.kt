package np.gov.mofaga.imis.family.service

import np.gov.mofaga.imis.family.exception.FamilyNotFoundException
import np.gov.mofaga.imis.family.repository.FamilyRepository
import np.gov.mofaga.imis.family.service.impl.FamilyServiceImpl
import np.gov.mofaga.imis.family.test.fixtures.FamilyTestFixtures
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class FamilyServiceTest {
    @Mock
    private lateinit var familyRepository: FamilyRepository

    @InjectMocks
    private lateinit var familyService: FamilyServiceImpl

    @Test
    fun `should create family successfully`() {
        // Given
        val request = FamilyTestFixtures.createFamilyRequest()
        val family = FamilyTestFixtures.createFamily()
        `when`(familyRepository.save(any())).thenReturn(family)

        // When
        val result = familyService.createFamily(request)

        // Then
        assertNotNull(result)
        assertEquals(family.headOfFamily, result.headOfFamily)
        verify(familyRepository).save(any())
    }

    @Test
    fun `should throw FamilyNotFoundException when family not found`() {
        // Given
        val id = UUID.randomUUID()
        `when`(familyRepository.findById(id)).thenReturn(Optional.empty())

        // Then
        assertThrows(FamilyNotFoundException::class.java) {
            familyService.getFamily(id)
        }
    }
}
