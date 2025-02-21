package np.gov.likhupikemun.dpms.location.repository.specification

import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import np.gov.likhupikemun.dpms.location.api.dto.criteria.WardSearchCriteria
import np.gov.likhupikemun.dpms.location.domain.Municipality
import np.gov.likhupikemun.dpms.location.domain.Municipality_
import np.gov.likhupikemun.dpms.location.domain.Ward
import np.gov.likhupikemun.dpms.location.domain.Ward_
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import kotlin.math.*

object WardSpecifications {
    fun withSearchCriteria(criteria: WardSearchCriteria): Specification<Ward> =
        Specification<Ward> { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            // Basic criteria
            criteria.municipalityCode?.let {
                val municipality: Join<Ward, Municipality> = root.join(Ward_.municipality)
                predicates.add(cb.equal(municipality.get(Municipality_.code), it))
            }

            criteria.wardNumber?.let {
                predicates.add(cb.equal(root.get<Int>(Ward_.wardNumber), it))
            }

            criteria.minPopulation?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Ward_.population), it))
            }

            criteria.maxPopulation?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get(Ward_.population), it))
            }

            criteria.minArea?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Ward_.area), it))
            }

            criteria.maxArea?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get(Ward_.area), it))
            }

            // Geographic search
            if (criteria.isGeographicSearch()) {
                val (minLat, maxLat, minLon, maxLon) =
                    calculateBoundingBox(
                        criteria.latitude!!.toDouble(),
                        criteria.longitude!!.toDouble(),
                        criteria.radiusKm!!,
                    )

                predicates.add(
                    cb.and(
                        cb.greaterThanOrEqualTo(root.get<BigDecimal>(Ward_.latitude), BigDecimal.valueOf(minLat)),
                        cb.lessThanOrEqualTo(root.get<BigDecimal>(Ward_.latitude), BigDecimal.valueOf(maxLat)),
                        cb.greaterThanOrEqualTo(root.get<BigDecimal>(Ward_.longitude), BigDecimal.valueOf(minLon)),
                        cb.lessThanOrEqualTo(root.get<BigDecimal>(Ward_.longitude), BigDecimal.valueOf(maxLon)),
                    ),
                )
            }

            // Combine all predicates
            if (predicates.isEmpty()) {
                null
            } else {
                cb.and(*predicates.toTypedArray())
            }
        }

    private fun calculateBoundingBox(
        lat: Double,
        lon: Double,
        radiusKm: Double,
    ): BoundingBox {
        val earthRadiusKm = 6371.0
        val latRadians = Math.toRadians(lat)

        val latKm = radiusKm / earthRadiusKm
        val lonKm = radiusKm / (earthRadiusKm * cos(latRadians))

        val latDelta = Math.toDegrees(latKm)
        val lonDelta = Math.toDegrees(lonKm)

        return BoundingBox(
            minLat = lat - latDelta,
            maxLat = lat + latDelta,
            minLon = lon - lonDelta,
            maxLon = lon + lonDelta,
        )
    }

    private data class BoundingBox(
        val minLat: Double,
        val maxLat: Double,
        val minLon: Double,
        val maxLon: Double,
    )
}
