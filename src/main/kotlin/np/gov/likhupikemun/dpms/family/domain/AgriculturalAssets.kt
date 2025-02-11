package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
class AgriculturalAssets(
    var landArea: BigDecimal, // in Ropani
    var hasIrrigation: Boolean = false,
    var livestockCount: Int = 0,
    var poultryCount: Int = 0,
    var hasGreenhouse: Boolean = false,
    var hasAgriculturalEquipment: Boolean = false,
)
