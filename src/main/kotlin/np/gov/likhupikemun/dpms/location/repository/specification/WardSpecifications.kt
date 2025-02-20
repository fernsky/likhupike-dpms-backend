package np.gov.likhupikemun.dpms.location.repository.specification

import np.gov.likhupikemun.dpms.location.api.dto.criteria.WardSearchCriteria
import np.gov.likhupikemun.dpms.location.domain.Ward
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import kotlin.math.*

object WardSpecifications {
    fun withSearchCriteria(criteria: WardSearchCriteria): Specification<Ward> =
        Specification<Ward> { root, query, cb ->
            val predicates =
                mutableListOf(
                    criteria.municipalityId?.let {
                        cb.equal(root.get<Any>("municipality").get<UUID>("id"), it)
                    },
                    criteria.wardNumber?.let {
                        cb.equal(root.get<Int>("wardNumber"), it)
                    },
                    if (!criteria.includeInactive) {
                        cb.isTrue(root.get("isActive"))
                    } else {
                        null
                    },
                    criteria.minPopulation?.let {
                        cb.greaterThanOrEqualTo(root.get("population"), it)
                    },
                    criteria.maxPopulation?.let {
                        cb.lessThanOrEqualTo(root.get("population"), it)
                    },
                    criteria.minArea?.let {
                        cb.greaterThanOrEqualTo(root.get("area"), it)
                    },
                    criteria.maxArea?.let {
                        cb.lessThanOrEqualTo(root.get("area"), it)
                    },
                ).filterNotNull()

            // Add geospatial search if coordinates are provided
            if (criteria.isGeographicSearch()) {
                val latitude = criteria.latitude!!
                val longitude = criteria.longitude!!
                val radiusKm = criteria.radiusKm!!

                // Calculate bounding box for initial filtering
                val (minLat, maxLat, minLon, maxLon) =
                    calculateBoundingBox(
                        latitude.toDouble(),
                        longitude.toDouble(),
                        radiusKm,
                    )

                predicates.add(
                    cb.and(
                        cb.greaterThanOrEqualTo(root.get("latitude"), BigDecimal.valueOf(minLat)),
                        cb.lessThanOrEqualTo(root.get("latitude"), BigDecimal.valueOf(maxLat)),
                        cb.greaterThanOrEqualTo(root.get("longitude"), BigDecimal.valueOf(minLon)),
                        cb.lessThanOrEqualTo(root.get("longitude"), BigDecimal.valueOf(maxLon)),
                    ),
                )
            }

            cb.and(*predicates.toTypedArray())
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
