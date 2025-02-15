package np.gov.likhupikemun.dpms.family.api.dto.request

import np.gov.likhupikemun.dpms.family.domain.enums.ConstructionType
import np.gov.likhupikemun.dpms.family.domain.enums.SocialCategory
import np.gov.likhupikemun.dpms.family.domain.enums.WaterSource
import java.math.BigDecimal

data class CreateFamilyRequest(
    val headOfFamily: String,
    val wardNumber: Int,
    val socialCategory: SocialCategory,
    val totalMembers: Int,
    val waterDetails: WaterDetailsRequest,
    val housingDetails: HousingDetailsRequest,
    val economicDetails: EconomicDetailsRequest,
    val agriculturalDetails: AgriculturalDetailsRequest,
    val latitude: Double?,
    val longitude: Double?,
)

data class WaterDetailsRequest(
    val primaryWaterSource: WaterSource,
    val hasWaterTreatmentSystem: Boolean,
    val distanceToWaterSource: Double?,
)

data class HousingDetailsRequest(
    val constructionType: ConstructionType,
    val totalRooms: Int,
    val hasElectricity: Boolean,
    val hasToilet: Boolean,
    val hasKitchenGarden: Boolean,
    val additionalDetails: String?,
)

data class EconomicDetailsRequest(
    val monthlyIncome: BigDecimal,
    val hasEmployedMembers: Boolean,
    val numberOfEmployedMembers: Int,
    val receivesSocialSecurity: Boolean,
    val hasBankAccount: Boolean,
    val hasLoans: Boolean,
)

data class AgriculturalDetailsRequest(
    val landArea: BigDecimal,
    val hasIrrigation: Boolean,
    val livestockCount: Int,
    val poultryCount: Int,
    val hasGreenhouse: Boolean,
    val hasAgriculturalEquipment: Boolean,
)
