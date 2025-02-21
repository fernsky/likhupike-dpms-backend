package np.gov.likhupikemun.dpms.location.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.common.entity.BaseEntity
import java.math.BigDecimal

@Entity
@Table(
    name = "districts",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_district_code_province",
            columnNames = ["code", "province_code"],
        ),
    ],
    indexes = [
        Index(name = "idx_districts_name", columnList = "name"),
        Index(name = "idx_districts_province", columnList = "province_code"),
    ],
)
class District : BaseEntity() {
    @Column(nullable = false, unique = true, length = 10)
    var code: String? = null

    @Column(nullable = false, length = 100)
    var name: String? = null

    @Column(nullable = false, length = 100)
    var nameNepali: String? = null

    @Column(precision = 10, scale = 2)
    var area: BigDecimal? = null

    @Column
    var population: Long? = null

    @Column(length = 50)
    var headquarter: String? = null

    @Column(length = 50)
    var headquarterNepali: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_code", nullable = false)
    var province: Province? = null

    @OneToMany(mappedBy = "district", cascade = [CascadeType.ALL])
    var municipalities: MutableSet<Municipality> = mutableSetOf()

    fun addMunicipality(municipality: Municipality) {
        municipalities.add(municipality)
        municipality.district = this
    }

    fun removeMunicipality(municipality: Municipality) {
        municipalities.remove(municipality)
        municipality.district = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is District) return false
        if (!super.equals(other)) return false
        return code == other.code && province?.id == other.province?.id
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (province?.id?.hashCode() ?: 0)
        return result
    }
}
