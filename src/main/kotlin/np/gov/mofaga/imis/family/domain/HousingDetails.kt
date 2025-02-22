package np.gov.mofaga.imis.family.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import np.gov.mofaga.imis.family.domain.enums.ConstructionType
import org.hibernate.annotations.Comment

@Embeddable
class HousingDetails {
    @Enumerated(EnumType.STRING)
    @Column(name = "construction_type", length = 50)
    @Comment("Type of house construction (e.g., CONCRETE, MUD, etc.)")
    var constructionType: ConstructionType? = null

    @Min(1)
    @Column(name = "total_rooms")
    @Comment("Total number of rooms in the house")
    var totalRooms: Int? = null

    @Column(name = "has_electricity")
    @Comment("Indicates if house has electricity connection")
    var hasElectricity: Boolean = false

    @Column(name = "has_toilet")
    @Comment("Indicates if house has toilet facility")
    var hasToilet: Boolean = false

    @Column(name = "has_kitchen_garden")
    @Comment("Indicates if house has kitchen garden")
    var hasKitchenGarden: Boolean = false

    @Size(max = 500)
    @Column(name = "additional_details", length = 500)
    @Comment("Any additional details about the housing")
    var additionalDetails: String? = null
}
