package np.gov.likhupikemun.dpms.location.domain

import jakarta.persistence.*
import np.gov.likhupikemun.dpms.common.entity.BaseEntity
import java.math.BigDecimal

@Entity
@Table(
    name = "wards",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_ward_number_municipality",
            columnNames = ["ward_number", "municipality_code"],
        ),
    ],
    indexes = [
        Index(name = "idx_wards_number", columnList = "ward_number"),
        Index(name = "idx_wards_municipality", columnList = "municipality_code"),
    ],
)
class Ward : BaseEntity() {
    @Column(name = "ward_number", nullable = false)
    var wardNumber: Int? = null

    @Column(precision = 10, scale = 2)
    var area: BigDecimal? = null

    @Column
    var population: Long? = null

    @Column(precision = 10, scale = 6)
    var latitude: BigDecimal? = null

    @Column(precision = 10, scale = 6)
    var longitude: BigDecimal? = null

    @Column(length = 100)
    var officeLocation: String? = null

    @Column(length = 100)
    var officeLocationNepali: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipality_code", nullable = false)
    var municipality: Municipality? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ward) return false
        if (!super.equals(other)) return false
        return wardNumber == other.wardNumber &&
            municipality?.id == other.municipality?.id
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (wardNumber ?: 0)
        result = 31 * result + (municipality?.id?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "Ward(number=$wardNumber, municipality=${municipality?.name})"
}
