package np.gov.likhupikemun.dpms.family.api.mapper

import np.gov.likhupikemun.dpms.family.api.dto.request.CreateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.response.*
import np.gov.likhupikemun.dpms.family.domain.*
import np.gov.likhupikemun.dpms.family.domain.enums.ConstructionType
import np.gov.likhupikemun.dpms.family.domain.enums.SocialCategory
import np.gov.likhupikemun.dpms.family.domain.enums.WaterSource
import java.math.BigDecimal
import java.time.ZoneId
import java.util.UUID

object FamilyMapper {
    fun toFamily(request: CreateFamilyRequest): Family =
        Family().apply {
            headOfFamily = request.headOfFamily
            wardNumber = request.wardNumber
            socialCategory = request.socialCategory
            totalMembers = request.totalMembers

            waterDetails =
                WaterDetails().apply {
                    primaryWaterSource = request.waterDetails.primaryWaterSource
                    hasWaterTreatmentSystem = request.waterDetails.hasWaterTreatmentSystem
                    distanceToWaterSource = request.waterDetails.distanceToWaterSource
                }

            housingDetails =
                HousingDetails().apply {
                    constructionType = request.housingDetails.constructionType
                    totalRooms = request.housingDetails.totalRooms
                    hasElectricity = request.housingDetails.hasElectricity
                    hasToilet = request.housingDetails.hasToilet
                    hasKitchenGarden = request.housingDetails.hasKitchenGarden
                    additionalDetails = request.housingDetails.additionalDetails
                }

            economicDetails =
                EconomicDetails().apply {
                    monthlyIncome = request.economicDetails.monthlyIncome
                    hasEmployedMembers = request.economicDetails.hasEmployedMembers
                    numberOfEmployedMembers = request.economicDetails.numberOfEmployedMembers
                    receivesSocialSecurity = request.economicDetails.receivesSocialSecurity
                    hasBankAccount = request.economicDetails.hasBankAccount
                    hasLoans = request.economicDetails.hasLoans
                }

            agriculturalAssets =
                AgriculturalAssets().apply {
                    landArea = request.agriculturalDetails.landArea
                    hasIrrigation = request.agriculturalDetails.hasIrrigation
                    livestockCount = request.agriculturalDetails.livestockCount
                    poultryCount = request.agriculturalDetails.poultryCount
                    hasGreenhouse = request.agriculturalDetails.hasGreenhouse
                    hasAgriculturalEquipment = request.agriculturalDetails.hasAgriculturalEquipment
                }

            latitude = request.latitude
            longitude = request.longitude
        }

    fun toResponse(family: Family): FamilyResponse =
        FamilyResponse(
            id = UUID.fromString(family.id.toString()),
            headOfFamily = family.headOfFamily ?: "",
            wardNumber = family.wardNumber ?: 0,
            socialCategory = family.socialCategory ?: SocialCategory.OTHER,
            totalMembers = family.totalMembers ?: 0,
            waterDetails =
                family.waterDetails?.let { details ->
                    WaterDetailsResponse(
                        primaryWaterSource = details.primaryWaterSource ?: WaterSource.OTHER,
                        hasWaterTreatmentSystem = details.hasWaterTreatmentSystem ?: false,
                        distanceToWaterSource = details.distanceToWaterSource,
                    )
                },
            housingDetails =
                family.housingDetails?.let { details ->
                    HousingDetailsResponse(
                        constructionType = details.constructionType ?: ConstructionType.OTHER,
                        totalRooms = details.totalRooms ?: 0,
                        hasElectricity = details.hasElectricity ?: false,
                        hasToilet = details.hasToilet ?: false,
                        hasKitchenGarden = details.hasKitchenGarden ?: false,
                        additionalDetails = details.additionalDetails,
                    )
                } ?: HousingDetailsResponse(
                    constructionType = ConstructionType.OTHER,
                    totalRooms = 0,
                    hasElectricity = false,
                    hasToilet = false,
                    hasKitchenGarden = false,
                    additionalDetails = null,
                ),
            economicDetails =
                family.economicDetails?.let { details ->
                    EconomicDetailsResponse(
                        monthlyIncome = details.monthlyIncome ?: BigDecimal.ZERO,
                        hasEmployedMembers = details.hasEmployedMembers ?: false,
                        numberOfEmployedMembers = details.numberOfEmployedMembers ?: 0,
                        receivesSocialSecurity = details.receivesSocialSecurity ?: false,
                        hasBankAccount = details.hasBankAccount ?: false,
                        hasLoans = details.hasLoans ?: false,
                    )
                } ?: EconomicDetailsResponse(
                    monthlyIncome = BigDecimal.ZERO,
                    hasEmployedMembers = false,
                    numberOfEmployedMembers = 0,
                    receivesSocialSecurity = false,
                    hasBankAccount = false,
                    hasLoans = false,
                ),
            agriculturalDetails =
                family.agriculturalAssets?.let { details ->
                    AgriculturalDetailsResponse(
                        landArea = details.landArea ?: BigDecimal.ZERO,
                        hasIrrigation = details.hasIrrigation ?: false,
                        livestockCount = details.livestockCount ?: 0,
                        poultryCount = details.poultryCount ?: 0,
                        hasGreenhouse = details.hasGreenhouse ?: false,
                        hasAgriculturalEquipment = details.hasAgriculturalEquipment ?: false,
                    )
                } ?: AgriculturalDetailsResponse(
                    landArea = BigDecimal.ZERO,
                    hasIrrigation = false,
                    livestockCount = 0,
                    poultryCount = 0,
                    hasGreenhouse = false,
                    hasAgriculturalEquipment = false,
                ),
            latitude = family.latitude,
            longitude = family.longitude,
            createdAt = family.createdAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
            updatedAt = family.updatedAt?.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
            photos = family.photos.map { toPhotoResponse(it) },
        )

    fun toPhotoResponse(photo: FamilyPhoto): FamilyPhotoResponse =
        FamilyPhotoResponse(
            id = UUID.fromString(photo.id.toString()),
            familyId = UUID.fromString(photo.family!!.id.toString()),
            fileName = photo.fileName!!,
            contentType = photo.contentType!!,
            fileSize = photo.fileSize!!,
            createdAt = photo.createdAt!!.atZone(ZoneId.systemDefault())?.toLocalDateTime(),
            thumbnailUrl = photo.thumbnailPath,
            url = photo.storagePath,
        )
}
