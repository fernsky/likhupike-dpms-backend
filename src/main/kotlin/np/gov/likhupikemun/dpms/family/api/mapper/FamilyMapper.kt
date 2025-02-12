package np.gov.likhupikemun.dpms.family.api.mapper

import np.gov.likhupikemun.dpms.family.api.dto.request.CreateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.response.*
import np.gov.likhupikemun.dpms.family.domain.*

object FamilyMapper {
    fun toFamily(request: CreateFamilyRequest): Family =
        Family(
            headOfFamily = request.headOfFamily,
            wardNumber = request.wardNumber,
            socialCategory = request.socialCategory,
            totalMembers = request.totalMembers,
            waterDetails =
                WaterDetails(
                    primaryWaterSource = request.waterDetails.primaryWaterSource,
                    hasWaterTreatmentSystem = request.waterDetails.hasWaterTreatmentSystem,
                    distanceToWaterSource = request.waterDetails.distanceToWaterSource,
                ),
            housingDetails =
                HousingDetails(
                    constructionType = request.housingDetails.constructionType,
                    totalRooms = request.housingDetails.totalRooms,
                    hasElectricity = request.housingDetails.hasElectricity,
                    hasToilet = request.housingDetails.hasToilet,
                    hasKitchenGarden = request.housingDetails.hasKitchenGarden,
                    additionalDetails = request.housingDetails.additionalDetails,
                ),
            economicDetails =
                EconomicDetails(
                    monthlyIncome = request.economicDetails.monthlyIncome,
                    hasEmployedMembers = request.economicDetails.hasEmployedMembers,
                    numberOfEmployedMembers = request.economicDetails.numberOfEmployedMembers,
                    receivesSocialSecurity = request.economicDetails.receivesSocialSecurity,
                    hasBankAccount = request.economicDetails.hasBankAccount,
                    hasLoans = request.economicDetails.hasLoans,
                ),
            agriculturalAssets =
                AgriculturalAssets(
                    landArea = request.agriculturalDetails.landArea,
                    hasIrrigation = request.agriculturalDetails.hasIrrigation,
                    livestockCount = request.agriculturalDetails.livestockCount,
                    poultryCount = request.agriculturalDetails.poultryCount,
                    hasGreenhouse = request.agriculturalDetails.hasGreenhouse,
                    hasAgriculturalEquipment = request.agriculturalDetails.hasAgriculturalEquipment,
                ),
            latitude = request.latitude,
            longitude = request.longitude,
        )

    fun toResponse(family: Family): FamilyResponse =
        FamilyResponse(
            id = family.id!!,
            headOfFamily = family.headOfFamily,
            wardNumber = family.wardNumber,
            socialCategory = family.socialCategory,
            totalMembers = family.totalMembers,
            waterDetails =
                WaterDetailsResponse(
                    primaryWaterSource = family.waterDetails.primaryWaterSource,
                    hasWaterTreatmentSystem = family.waterDetails.hasWaterTreatmentSystem,
                    distanceToWaterSource = family.waterDetails.distanceToWaterSource,
                ),
            housingDetails =
                HousingDetailsResponse(
                    constructionType = family.housingDetails.constructionType,
                    totalRooms = family.housingDetails.totalRooms,
                    hasElectricity = family.housingDetails.hasElectricity,
                    hasToilet = family.housingDetails.hasToilet,
                    hasKitchenGarden = family.housingDetails.hasKitchenGarden,
                    additionalDetails = family.housingDetails.additionalDetails,
                ),
            economicDetails =
                EconomicDetailsResponse(
                    monthlyIncome = family.economicDetails.monthlyIncome,
                    hasEmployedMembers = family.economicDetails.hasEmployedMembers,
                    numberOfEmployedMembers = family.economicDetails.numberOfEmployedMembers,
                    receivesSocialSecurity = family.economicDetails.receivesSocialSecurity,
                    hasBankAccount = family.economicDetails.hasBankAccount,
                    hasLoans = family.economicDetails.hasLoans,
                ),
            agriculturalDetails =
                AgriculturalDetailsResponse(
                    landArea = family.agriculturalAssets.landArea,
                    hasIrrigation = family.agriculturalAssets.hasIrrigation,
                    livestockCount = family.agriculturalAssets.livestockCount,
                    poultryCount = family.agriculturalAssets.poultryCount,
                    hasGreenhouse = family.agriculturalAssets.hasGreenhouse,
                    hasAgriculturalEquipment = family.agriculturalAssets.hasAgriculturalEquipment,
                ),
            latitude = family.latitude,
            longitude = family.longitude,
            createdAt = family.createdAt,
            updatedAt = family.updatedAt,
            photos = family.photos.map { toPhotoResponse(it) },
        )

    fun toPhotoResponse(photo: FamilyPhoto): FamilyPhotoResponse =
        FamilyPhotoResponse(
            id = photo.id!!,
            familyId = photo.family.id!!,
            fileName = photo.fileName,
            contentType = photo.contentType,
            fileSize = photo.fileSize,
            createdAt = photo.createdAt,
        )
}
