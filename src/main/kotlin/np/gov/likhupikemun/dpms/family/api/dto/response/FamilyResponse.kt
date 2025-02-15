package np.gov.likhupikemun.dpms.family.api.dto.response

import np.gov.likhupikemun.dpms.family.domain.enums.ConstructionType
import np.gov.likhupikemun.dpms.family.domain.enums.SocialCategory
import np.gov.likhupikemun.dpms.family.domain.enums.WaterSource
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class FamilyResponse(
    val id: UUID,
    val headOfFamily: String,
    val wardNumber: Int,
    val socialCategory: SocialCategory,
    val totalMembers: Int,
    val waterDetails: WaterDetailsResponse?,
    val housingDetails: HousingDetailsResponse,
    val economicDetails: EconomicDetailsResponse,
    val agriculturalDetails: AgriculturalDetailsResponse,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val photos: List<FamilyPhotoResponse>,
)

data class WaterDetailsResponse(
    val primaryWaterSource: WaterSource,
    val hasWaterTreatmentSystem: Boolean,
    val distanceToWaterSource: Double?,
)

data class HousingDetailsResponse(
    val constructionType: ConstructionType,
    val totalRooms: Int,
    val hasElectricity: Boolean,
    val hasToilet: Boolean,
    val hasKitchenGarden: Boolean,
    val additionalDetails: String?,
)

data class EconomicDetailsResponse(
    val monthlyIncome: BigDecimal,
    val hasEmployedMembers: Boolean,
    val numberOfEmployedMembers: Int,
    val receivesSocialSecurity: Boolean,
    val hasBankAccount: Boolean,
    val hasLoans: Boolean,
)

data class AgriculturalDetailsResponse(
    val landArea: BigDecimal,
    val hasIrrigation: Boolean,
    val livestockCount: Int,
    val poultryCount: Int,
    val hasGreenhouse: Boolean,
    val hasAgriculturalEquipment: Boolean,
)
