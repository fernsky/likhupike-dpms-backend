package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import np.gov.likhupikemun.dpms.family.domain.enums.WaterSource
import org.hibernate.annotations.Comment

@Embeddable
class WaterDetails {
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_water_source", length = 50)
    @Comment("Primary source of water for the family")
    var primaryWaterSource: WaterSource? = null

    @Column(name = "has_water_treatment")
    @Comment("Indicates if family has water treatment system")
    var hasWaterTreatmentSystem: Boolean = false

    @Min(0)
    @Column(name = "distance_to_water", precision = 10)
    @Comment("Distance to water source in meters")
    var distanceToWaterSource: Double? = null
}
