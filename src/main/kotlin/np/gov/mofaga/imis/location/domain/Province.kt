package np.gov.mofaga.imis.location.domain

import jakarta.persistence.*
import np.gov.mofaga.imis.common.entity.BaseEntity
import java.math.BigDecimal

@Entity
@Table(
    name = "provinces",
    indexes = [
        Index(name = "idx_provinces_name", columnList = "name"),
        Index(name = "idx_provinces_code", columnList = "code"),
    ],
)
class Province : BaseEntity() {
    @Column(nullable = false, length = 100)
    var name: String? = null

    @Column(nullable = false, length = 100)
    var nameNepali: String? = null

    @Column(nullable = false, unique = true, length = 36)
    var code: String? = null

    @Column(precision = 10, scale = 2)
    var area: BigDecimal? = null

    @Column
    var population: Long? = null

    @Column(length = 50)
    var headquarter: String? = null

    @Column(length = 50)
    var headquarterNepali: String? = null

    @OneToMany(mappedBy = "province", cascade = [CascadeType.ALL])
    var districts: MutableSet<District> = mutableSetOf()

    fun addDistrict(district: District) {
        districts.add(district)
        district.province = this
    }

    fun removeDistrict(district: District) {
        districts.remove(district)
        district.province = null
    }
}
