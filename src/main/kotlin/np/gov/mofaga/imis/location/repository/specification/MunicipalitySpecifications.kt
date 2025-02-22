package np.gov.mofaga.imis.location.repository.specification

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import np.gov.mofaga.imis.location.api.dto.criteria.MunicipalitySearchCriteria
import np.gov.mofaga.imis.location.api.dto.enums.MunicipalitySortField
import np.gov.mofaga.imis.location.domain.District_
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Municipality_
import np.gov.mofaga.imis.location.domain.Province_
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal

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
            criteria.districtCode?.let { districtCode ->
                val district = root.join(Municipality_.district)
                predicates.add(cb.equal(district.get(District_.code), districtCode))
            }

            criteria.provinceCode?.let { provinceCode ->
                val district = root.join(Municipality_.district)
                val province = district.join(District_.province)
                predicates.add(cb.equal(province.get(Province_.code), provinceCode))
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

            // Apply sorting if query is not a count query
            if (query?.resultType != Long::class.java) {
                query?.let { q ->
                    when (criteria.sortBy) {
                        MunicipalitySortField.DISTANCE -> {
                            // Handle distance-based sorting with null check
                            val order =
                                if (criteria.latitude != null && criteria.longitude != null) {
                                    val distanceExpression =
                                        createDistanceExpression(
                                            cb,
                                            root,
                                            criteria.latitude,
                                            criteria.longitude,
                                        )
                                    if (criteria.sortDirection.isAscending) {
                                        cb.asc(distanceExpression)
                                    } else {
                                        cb.desc(distanceExpression)
                                    }
                                } else {
                                    // Fallback sort if coordinates are missing
                                    if (criteria.sortDirection.isAscending) {
                                        cb.asc(root.get<Any>("id"))
                                    } else {
                                        cb.desc(root.get<Any>("id"))
                                    }
                                }
                            q.orderBy(order)
                        }
                        else -> {
                            val sortField = criteria.sortBy.toEntityField()
                            val order =
                                if (criteria.sortDirection.isAscending) {
                                    cb.asc(root.get<Any>(sortField))
                                } else {
                                    cb.desc(root.get<Any>(sortField))
                                }
                            q.orderBy(order)
                        }
                    }
                }
            }

            if (predicates.isEmpty()) {
                null
            } else {
                cb.and(*predicates.toTypedArray())
            }
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
