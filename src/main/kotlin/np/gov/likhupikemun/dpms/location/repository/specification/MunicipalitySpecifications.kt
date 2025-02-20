package np.gov.likhupikemun.dpms.location.repository.specification

import np.gov.likhupikemun.dpms.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.likhupikemun.dpms.location.domain.District_
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Municipality_
import np.gov.likhupikemun.dpms.location.domain.Province_
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

object MunicipalitySpecifications {
    fun withSearchCriteria(criteria: MunicipalitySearchCriteria): Specification<Municipality> =
        Specification { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            // Text search
            criteria.searchTerm?.let { term ->
                val searchTerm = "%${term.lowercase()}%"
                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get(Municipality_.name)), searchTerm),
                        cb.like(cb.lower(root.get(Municipality_.nameNepali)), searchTerm),
                        cb.like(cb.lower(root.get(Municipality_.code)), searchTerm),
                    ),
                )
            }

            // Code exact match
            criteria.code?.let { code ->
                predicates.add(
                    cb.equal(
                        cb.lower(root.get(Municipality_.code)),
                        code.lowercase(),
                    ),
                )
            }

            // District and Province filtering
            criteria.districtId?.let { districtId ->
                val district = root.join(Municipality_.district)
                predicates.add(cb.equal(district.get(District_.id), districtId))
            }

            criteria.provinceId?.let { provinceId ->
                val district = root.join(Municipality_.district)
                val province = district.join(District_.province)
                predicates.add(cb.equal(province.get(Province_.id), provinceId))
            }

            // Municipality type filtering
            criteria.types?.let { types ->
                if (types.isNotEmpty()) {
                    predicates.add(root.get(Municipality_.type).`in`(types))
                }
            }

            // Ward count range
            criteria.minWards?.let { min ->
                predicates.add(cb.greaterThanOrEqualTo(root.get(Municipality_.totalWards), min))
            }
            criteria.maxWards?.let { max ->
                predicates.add(cb.lessThanOrEqualTo(root.get(Municipality_.totalWards), max))
            }

            // Population range
            criteria.minPopulation?.let { min ->
                predicates.add(cb.greaterThanOrEqualTo(root.get(Municipality_.population), min))
            }
            criteria.maxPopulation?.let { max ->
                predicates.add(cb.lessThanOrEqualTo(root.get(Municipality_.population), max))
            }

            // Area range
            criteria.minArea?.let { min ->
                predicates.add(cb.greaterThanOrEqualTo(root.get(Municipality_.area), min))
            }
            criteria.maxArea?.let { max ->
                predicates.add(cb.lessThanOrEqualTo(root.get(Municipality_.area), max))
            }

            // Geo-spatial search
            if (criteria.latitude != null && criteria.longitude != null && criteria.radiusKm != null) {
                val distanceInMeters = criteria.radiusKm * 1000 // Convert km to meters
                predicates.add(
                    createDistancePredicate(
                        cb,
                        root,
                        criteria.latitude,
                        criteria.longitude,
                        distanceInMeters,
                    ),
                )
            }

            // Always include active status check
            predicates.add(cb.isTrue(root.get(Municipality_.isActive)))

            // Handle sorting, including special case for distance-based sorting
            if (!query.groupList.isEmpty()) {
                when (criteria.sortBy) {
                    MunicipalitySortField.DISTANCE -> {
                        if (criteria.latitude != null && criteria.longitude != null) {
                            val distanceExpression =
                                createDistanceExpression(
                                    cb,
                                    root,
                                    criteria.latitude,
                                    criteria.longitude,
                                )
                            if (criteria.sortDirection.isAscending) {
                                query.orderBy(cb.asc(distanceExpression))
                            } else {
                                query.orderBy(cb.desc(distanceExpression))
                            }
                        }
                    }
                    else -> {
                        val sortField = criteria.sortBy.toEntityField()
                        if (criteria.sortDirection.isAscending) {
                            query.orderBy(cb.asc(root.get<Any>(sortField)))
                        } else {
                            query.orderBy(cb.desc(root.get<Any>(sortField)))
                        }
                    }
                }
            }

            cb.and(*predicates.toTypedArray())
        }

    private fun createDistancePredicate(
        cb: CriteriaBuilder,
        root: Root<Municipality>,
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
    ): Predicate {
        val distanceFunction =
            cb.function(
                "ST_Distance_Sphere",
                Double::class.java,
                cb.function(
                    "point",
                    Any::class.java,
                    root.get<BigDecimal>(Municipality_.longitude),
                    root.get<BigDecimal>(Municipality_.latitude),
                ),
                cb.function(
                    "point",
                    Any::class.java,
                    cb.literal(longitude),
                    cb.literal(latitude),
                ),
            )
        return cb.le(distanceFunction, radiusInMeters)
    }

    private fun createDistanceExpression(
        cb: CriteriaBuilder,
        root: Root<Municipality>,
        latitude: BigDecimal,
        longitude: BigDecimal,
    ): Expression<Double> =
        cb.function(
            "ST_Distance_Sphere",
            Double::class.java,
            cb.function(
                "point",
                Any::class.java,
                root.get<BigDecimal>(Municipality_.longitude),
                root.get<BigDecimal>(Municipality_.latitude),
            ),
            cb.function(
                "point",
                Any::class.java,
                cb.literal(longitude),
                cb.literal(latitude),
            ),
        )
}
