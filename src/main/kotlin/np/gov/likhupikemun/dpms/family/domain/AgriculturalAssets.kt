package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Comment
import java.math.BigDecimal

@Embeddable
class AgriculturalAssets {
    @PositiveOrZero
    @Column(name = "land_area", precision = 10)
    @Comment("Total agricultural land area in Ropani")
    var landArea: BigDecimal? = null

    @Column(name = "has_irrigation")
    @Comment("Indicates if land has irrigation facilities")
    var hasIrrigation: Boolean = false

    @Min(0)
    @Column(name = "livestock_count")
    @Comment("Number of livestock animals")
    var livestockCount: Int = 0

    @Min(0)
    @Column(name = "poultry_count")
    @Comment("Number of poultry birds")
    var poultryCount: Int = 0

    @Column(name = "has_greenhouse")
    @Comment("Indicates if family has greenhouse")
    var hasGreenhouse: Boolean = false

    @Column(name = "has_agricultural_equipment")
    @Comment("Indicates if family owns agricultural equipment")
    var hasAgriculturalEquipment: Boolean = false

    @Size(max = 500)
    @Column(name = "equipment_details", length = 500)
    @Comment("Details of agricultural equipment owned")
    var equipmentDetails: String? = null

    @Column(name = "cultivated_crops", length = 255)
    @Comment("List of crops currently being cultivated")
    var cultivatedCrops: String? = null
}
