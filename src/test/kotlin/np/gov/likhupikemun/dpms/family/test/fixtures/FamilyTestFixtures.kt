package np.gov.likhupikemun.dpms.family.test.fixtures

import np.gov.likhupikemun.dpms.family.api.dto.request.*
import np.gov.likhupikemun.dpms.family.domain.*
import np.gov.likhupikemun.dpms.family.domain.enums.*
import java.math.BigDecimal
import java.util.*

object FamilyTestFixtures {
    fun createFamily(
        id: UUID? = UUID.randomUUID(),
        headOfFamily: String = "John Doe",
        wardNumber: Int = 1,
    ) = Family(
        id = id,
        headOfFamily = headOfFamily,
        wardNumber = wardNumber,
        socialCategory = SocialCategory.GENERAL,
        totalMembers = 4,
        waterDetails =
            WaterDetails(
                primaryWaterSource = WaterSource.TAP_WATER,
                hasWaterTreatmentSystem = true,
                distanceToWaterSource = 100.0,
            ),
        housingDetails =
            HousingDetails(
                constructionType = ConstructionType.BRICK_AND_MORTAR,
                totalRooms = 3,
                hasElectricity = true,
                hasToilet = true,
                hasKitchenGarden = true,
            ),
        economicDetails =
            EconomicDetails(
                monthlyIncome = BigDecimal("25000.00"),
                hasEmployedMembers = true,
                numberOfEmployedMembers = 2,
            ),
        agriculturalAssets =
            AgriculturalAssets(
                landArea = BigDecimal("5.0"),
                hasIrrigation = true,
                livestockCount = 2,
                poultryCount = 5,
            ),
    )

    fun createFamilyRequest(
        headOfFamily: String = "John Doe",
        wardNumber: Int = 1,
    ) = CreateFamilyRequest(
        headOfFamily = headOfFamily,
        wardNumber = wardNumber,
        socialCategory = SocialCategory.GENERAL,
        totalMembers = 4,
        waterDetails =
            WaterDetailsRequest(
                primaryWaterSource = WaterSource.TAP_WATER,
                hasWaterTreatmentSystem = true,
                distanceToWaterSource = 100.0,
            ),
        housingDetails =
            HousingDetailsRequest(
                constructionType = ConstructionType.BRICK_AND_MORTAR,
                totalRooms = 3,
                hasElectricity = true,
                hasToilet = true,
                hasKitchenGarden = true,
                additionalDetails = null,
            ),
        economicDetails =
            EconomicDetailsRequest(
                monthlyIncome = BigDecimal("25000.00"),
                hasEmployedMembers = true,
                numberOfEmployedMembers = 2,
                receivesSocialSecurity = false,
                hasBankAccount = true,
                hasLoans = false,
            ),
        agriculturalDetails =
            AgriculturalDetailsRequest(
                landArea = BigDecimal("5.0"),
                hasIrrigation = true,
                livestockCount = 2,
                poultryCount = 5,
                hasGreenhouse = false,
                hasAgriculturalEquipment = true,
            ),
        latitude = 27.7172,
        longitude = 85.3240,
    )
}
