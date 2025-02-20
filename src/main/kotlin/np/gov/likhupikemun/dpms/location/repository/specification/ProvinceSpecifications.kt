package np.gov.likhupikemun.dpms.location.repository.specification

import np.gov.likhupikemun.dpms.location.api.dto.criteria.ProvinceSearchCriteria
import np.gov.likhupikemun.dpms.location.domain.Province
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.Predicate

object ProvinceSpecifications {
    fun withSearchCriteria(criteria: ProvinceSearchCriteria): Specification<Province> =
        Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            // Generic search term (matches name, nameNepali, or headquarter)
            criteria.searchTerm?.let { term ->
                val searchTerm = "%${term.lowercase()}%"
                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get("name")), searchTerm),
                        cb.like(cb.lower(root.get("nameNepali")), searchTerm),
                        cb.like(cb.lower(root.get("headquarter")), searchTerm),
                    ),
                )
            }

            // Code exact match (case-insensitive)
            criteria.code?.let { code ->
                predicates.add(
                    cb.equal(
                        cb.lower(root.get("code")),
                        code.lowercase(),
                    ),
                )
            }

            cb.and(*predicates.toTypedArray())
        }

    fun hasMinimumPopulation(minPopulation: Long): Specification<Province> =
        Specification { root, _, cb ->
            cb.greaterThanOrEqualTo(root.get("population"), minPopulation)
        }

    fun hasMinimumArea(minArea: BigDecimal): Specification<Province> =
        Specification { root, _, cb ->
            cb.greaterThanOrEqualTo(root.get("area"), minArea)
        }
}
