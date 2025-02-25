package np.gov.mofaga.imis.location.repository.specification

import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Predicate
import np.gov.mofaga.imis.location.api.dto.criteria.WardSearchCriteria
import np.gov.mofaga.imis.location.domain.*
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import kotlin.math.*

object WardSpecifications {
    fun withSearchCriteria(criteria: WardSearchCriteria): Specification<Ward> =
        Specification<Ward> { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            // Municipality filter
            criteria.municipalityCode?.let { code ->
                val municipality: Join<Ward, Municipality> = root.join(Ward_.municipality)
                predicates.add(
                    cb.equal(
                        cb.lower(municipality.get(Municipality_.code)),
                        code.lowercase(),
                    ),
                )
            }

            // District filter
            criteria.districtCode?.let { code ->
                val municipality: Join<Ward, Municipality> = root.join(Ward_.municipality)
                val district: Join<Municipality, District> = municipality.join(Municipality_.district)
                predicates.add(
                    cb.equal(
                        cb.lower(district.get(District_.code)),
                        code.lowercase(),
                    ),
                )
            }

            // Province filter
            criteria.provinceCode?.let { code ->
                val municipality: Join<Ward, Municipality> = root.join(Ward_.municipality)
                val district: Join<Municipality, District> = municipality.join(Municipality_.district)
                val province: Join<District, Province> = district.join(District_.province)
                predicates.add(
                    cb.equal(
                        cb.lower(province.get(Province_.code)),
                        code.lowercase(),
                    ),
                )
            }

            // Basic criteria
            criteria.wardNumber?.let {
                predicates.add(cb.equal(root.get<Int>(Ward_.wardNumber), it))
            }

            // Population range
            criteria.minPopulation?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Ward_.population), it))
            }
            criteria.maxPopulation?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get(Ward_.population), it))
            }

            // Area range
            criteria.minArea?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Ward_.area), it))
            }
            criteria.maxArea?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get(Ward_.area), it))
            }

            // Ward number range
            criteria.wardNumberFrom?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Ward_.wardNumber), it))
            }
            criteria.wardNumberTo?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get(Ward_.wardNumber), it))
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
