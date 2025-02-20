package np.gov.likhupikemun.dpms.location.repository.specification

import np.gov.likhupikemun.dpms.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.District_
import org.springframework.data.jpa.domain.Specification
import javax.persistence.criteria.Predicate

object DistrictSpecifications {
    fun withSearchCriteria(criteria: DistrictSearchCriteria): Specification<District> =
        Specification { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            // Generic search term (matches name, nameNepali, or headquarter)
            criteria.searchTerm?.let { term ->
                val searchTerm = "%${term.lowercase()}%"
                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get(District_.name)), searchTerm),
                        cb.like(cb.lower(root.get(District_.nameNepali)), searchTerm),
                        cb.like(cb.lower(root.get(District_.headquarter)), searchTerm),
                    ),
                )
            }

            // Code exact match (case-insensitive)
            criteria.code?.let { code ->
                predicates.add(
                    cb.equal(
                        cb.lower(root.get(District_.code)),
                        code.lowercase(),
                    ),
                )
            }

            // Always include active status check
            predicates.add(cb.isTrue(root.get(District_.isActive)))

            // Apply sorting
            val sortField = criteria.sortBy.toEntityField()
            if (criteria.sortDirection.isAscending) {
                query.orderBy(cb.asc(root.get<Any>(sortField)))
            } else {
                query.orderBy(cb.desc(root.get<Any>(sortField)))
            }

            cb.and(*predicates.toTypedArray())
        }
}
