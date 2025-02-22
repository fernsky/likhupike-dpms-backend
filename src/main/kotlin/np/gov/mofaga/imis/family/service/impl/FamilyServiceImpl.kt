package np.gov.mofaga.imis.family.service.impl

import np.gov.mofaga.imis.family.api.dto.request.CreateFamilyRequest
import np.gov.mofaga.imis.family.api.dto.request.FamilySearchCriteria
import np.gov.mofaga.imis.family.api.dto.request.UpdateFamilyRequest
import np.gov.mofaga.imis.family.api.dto.response.FamilyResponse
import np.gov.mofaga.imis.family.api.mapper.FamilyMapper
import np.gov.mofaga.imis.family.domain.Family
import np.gov.mofaga.imis.family.exception.FamilyNotFoundException
import np.gov.mofaga.imis.family.repository.FamilyRepository
import np.gov.mofaga.imis.family.repository.specification.FamilySpecifications
import np.gov.mofaga.imis.family.service.FamilyService
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class FamilyServiceImpl(
    private val familyRepository: FamilyRepository,
) : FamilyService {
    @Transactional
    override fun createFamily(request: CreateFamilyRequest): FamilyResponse {
        val family = FamilyMapper.toFamily(request)
        return FamilyMapper.toResponse(familyRepository.save(family))
    }

    @Transactional
    override fun updateFamily(
        id: UUID,
        request: UpdateFamilyRequest,
    ): FamilyResponse {
        val family = getFamilyEntity(id)
        updateFamilyFromRequest(family, request)
        return FamilyMapper.toResponse(familyRepository.save(family))
    }

    @Transactional(readOnly = true)
    override fun getFamily(id: UUID): FamilyResponse = FamilyMapper.toResponse(getFamilyEntity(id))

    @Transactional(readOnly = true)
    override fun searchFamilies(criteria: FamilySearchCriteria): Page<FamilyResponse> =
        familyRepository
            .findAll(FamilySpecifications.withSearchCriteria(criteria), criteria.toPageable())
            .map(FamilyMapper::toResponse)

    @Transactional
    override fun deleteFamily(id: UUID) {
        if (!familyRepository.existsById(id)) {
            throw FamilyNotFoundException()
        }
        familyRepository.deleteById(id)
    }

    private fun getFamilyEntity(id: UUID): Family = familyRepository.findById(id).orElseThrow { FamilyNotFoundException() }

    private fun updateFamilyFromRequest(
        family: Family,
        request: UpdateFamilyRequest,
    ) {
        family.apply {
            headOfFamily = request.headOfFamily
            totalMembers = request.totalMembers
            waterDetails?.apply {
                primaryWaterSource = request.waterDetails.primaryWaterSource
                hasWaterTreatmentSystem = request.waterDetails.hasWaterTreatmentSystem
                distanceToWaterSource = request.waterDetails.distanceToWaterSource
            }
            housingDetails?.apply {
                constructionType = request.housingDetails.constructionType
                totalRooms = request.housingDetails.totalRooms
                hasElectricity = request.housingDetails.hasElectricity
                hasToilet = request.housingDetails.hasToilet
                hasKitchenGarden = request.housingDetails.hasKitchenGarden
                additionalDetails = request.housingDetails.additionalDetails
            }
            economicDetails?.apply {
                monthlyIncome = request.economicDetails.monthlyIncome
                hasEmployedMembers = request.economicDetails.hasEmployedMembers
                numberOfEmployedMembers = request.economicDetails.numberOfEmployedMembers
                receivesSocialSecurity = request.economicDetails.receivesSocialSecurity
                hasBankAccount = request.economicDetails.hasBankAccount
                hasLoans = request.economicDetails.hasLoans
            }
            agriculturalAssets?.apply {
                landArea = request.agriculturalDetails.landArea
                hasIrrigation = request.agriculturalDetails.hasIrrigation
                livestockCount = request.agriculturalDetails.livestockCount
                poultryCount = request.agriculturalDetails.poultryCount
                hasGreenhouse = request.agriculturalDetails.hasGreenhouse
                hasAgriculturalEquipment = request.agriculturalDetails.hasAgriculturalEquipment
            }
            latitude = request.latitude
            longitude = request.longitude
            updatedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        }
    }
}
