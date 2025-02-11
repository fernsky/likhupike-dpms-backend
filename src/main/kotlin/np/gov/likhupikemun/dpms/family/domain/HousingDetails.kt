package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.family.domain.enums.ConstructionType

@Embeddable
class HousingDetails(
    @Enumerated(EnumType.STRING)
    var constructionType: ConstructionType,
    var totalRooms: Int,
    var hasElectricity: Boolean = false,
    var hasToilet: Boolean = false,
    var hasKitchenGarden: Boolean = false,
    @Column(length = 500)
    var additionalDetails: String? = null,
)
