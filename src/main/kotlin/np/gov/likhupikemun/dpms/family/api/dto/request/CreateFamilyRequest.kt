package np.gov.likhupikemun.dpms.family.api.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.family.domain.enums.ConstructionType
import np.gov.likhupikemun.dpms.family.domain.enums.SocialCategory
import np.gov.likhupikemun.dpms.family.domain.enums.WaterSource
import np.gov.likhupikemun.dpms.shared.validation.annotations.ValidGpsCoordinates
import java.math.BigDecimal

data class CreateFamilyRequest(
    @field:NotBlank
    val headOfFamily: String,
    @field:Min(1) @field:Max(32)
    val wardNumber: Int,
    @field:NotNull
    val socialCategory: SocialCategory,
    @field:Min(1)
    val totalMembers: Int,
    @field:Valid
    val waterDetails: WaterDetailsRequest,
    @field:Valid
    val housingDetails: HousingDetailsRequest,
    @field:Valid
    val economicDetails: EconomicDetailsRequest,
    @field:Valid
    val agriculturalDetails: AgriculturalDetailsRequest,
    @field:ValidGpsCoordinates
    val latitude: Double?,
    @field:ValidGpsCoordinates
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
    @field:Positive
    val monthlyIncome: BigDecimal,
    val hasEmployedMembers: Boolean,
    val numberOfEmployedMembers: Int,
    val receivesSocialSecurity: Boolean,
    val hasBankAccount: Boolean,
    val hasLoans: Boolean,
)

data class AgriculturalDetailsRequest(
    @field:PositiveOrZero
    val landArea: BigDecimal,
    val hasIrrigation: Boolean,
    val livestockCount: Int,
    val poultryCount: Int,
    val hasGreenhouse: Boolean,
    val hasAgriculturalEquipment: Boolean,
)
