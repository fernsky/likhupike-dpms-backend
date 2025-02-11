package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.family.domain.enums.SocialCategory
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "families")
class Family(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @Column(nullable = false)
    var headOfFamily: String,
    @Column(nullable = false)
    var wardNumber: Int,
    @Enumerated(EnumType.STRING)
    var socialCategory: SocialCategory,
    var totalMembers: Int,
    @Embedded
    var waterDetails: WaterDetails,
    @Embedded
    var housingDetails: HousingDetails,
    @Embedded
    var economicDetails: EconomicDetails,
    @Embedded
    var agriculturalAssets: AgriculturalAssets,
    var latitude: Double? = null,
    var longitude: Double? = null,
    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime? = null,
    @OneToMany(mappedBy = "family", cascade = [CascadeType.ALL], orphanRemoval = true)
    var photos: MutableList<FamilyPhoto> = mutableListOf(),
)
