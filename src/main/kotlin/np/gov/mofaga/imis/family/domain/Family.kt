package np.gov.mofaga.imis.family.domain

import jakarta.persistence.*
import np.gov.mofaga.imis.common.entity.BaseEntity
import np.gov.mofaga.imis.family.domain.enums.SocialCategory
import org.hibernate.annotations.DynamicUpdate

@Entity
@DynamicUpdate
@Table(
    name = "families",
    indexes = [
        Index(name = "idx_families_ward", columnList = "ward_number"),
        Index(name = "idx_families_head", columnList = "head_of_family"),
    ],
)
class Family : BaseEntity() {
    @Column(name = "head_of_family", nullable = false)
    var headOfFamily: String? = null

    @Column(name = "ward_number", nullable = false)
    var wardNumber: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "social_category")
    var socialCategory: SocialCategory? = null

    @Column(name = "total_members")
    var totalMembers: Int? = null

    @Embedded
    var waterDetails: WaterDetails? = null

    @Embedded
    var housingDetails: HousingDetails? = null

    @Embedded
    var economicDetails: EconomicDetails? = null

    @Embedded
    var agriculturalAssets: AgriculturalAssets? = null

    var latitude: Double? = null
    var longitude: Double? = null

    @OneToMany(mappedBy = "family", cascade = [CascadeType.ALL], orphanRemoval = true)
    var photos: MutableList<FamilyPhoto> = mutableListOf()
}
