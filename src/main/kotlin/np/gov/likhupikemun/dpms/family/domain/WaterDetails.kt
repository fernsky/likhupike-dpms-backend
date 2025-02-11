package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.family.domain.enums.WaterSource

@Embeddable
class WaterDetails(
    @Enumerated(EnumType.STRING)
    var primaryWaterSource: WaterSource,
    var hasWaterTreatmentSystem: Boolean = false,
    var distanceToWaterSource: Double? = null,
)
