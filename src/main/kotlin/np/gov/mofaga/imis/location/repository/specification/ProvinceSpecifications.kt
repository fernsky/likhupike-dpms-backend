package np.gov.mofaga.imis.location.repository.specification

import jakarta.persistence.criteria.Predicate
import np.gov.mofaga.imis.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.domain.Province_
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal

object ProvinceSpecifications {
    fun withSearchCriteria(criteria: ProvinceSearchCriteria): Specification<Province> =
        Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            // Generic search term (matches name, nameNepali, or headquarter)
            criteria.searchTerm?.let { term ->
                val searchTerm = "%${term.lowercase()}%"
                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get(Province_.name)), searchTerm),
                        cb.like(cb.lower(root.get(Province_.nameNepali)), searchTerm),
                        cb.like(cb.lower(root.get(Province_.headquarter)), searchTerm),
                    ),
                )
            }

            // Code exact match (case-insensitive)
            criteria.code?.let { code ->
                predicates.add(
                    cb.equal(
                        cb.lower(root.get(Province_.code)),
                        code.lowercase(),
                    ),
                )
            }

            cb.and(*predicates.toTypedArray())
        }

    fun hasMinimumPopulation(minPopulation: Long): Specification<Province> =
        Specification { root, _, cb ->
            cb.greaterThanOrEqualTo(root.get(Province_.population), minPopulation)
        }

    fun hasMinimumArea(minArea: BigDecimal): Specification<Province> =
        Specification { root, _, cb ->
            cb.greaterThanOrEqualTo(root.get(Province_.area), minArea)
        }
}
