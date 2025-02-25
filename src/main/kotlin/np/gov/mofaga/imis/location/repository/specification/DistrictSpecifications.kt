package np.gov.mofaga.imis.location.repository.specification

import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Order
import jakarta.persistence.criteria.Predicate
import np.gov.mofaga.imis.location.api.dto.criteria.DistrictSearchCriteria
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.District_
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.domain.Province_
import org.springframework.data.jpa.domain.Specification

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

            // Province code filter
            criteria.provinceCode?.let { provinceCode ->
                val province: Join<District, Province> = root.join(District_.province)
                predicates.add(
                    cb.equal(
                        cb.lower(province.get(Province_.code)),
                        provinceCode.lowercase()
                    )
                )
            }

            // Apply sorting if query is not a count query and is a CriteriaQuery
            query?.let { q ->
                if (q is CriteriaQuery<*> && q.resultType != Long::class.java) {
                    val sortField = criteria.sortBy.toEntityField()
                    val order: Order =
                        if (criteria.sortDirection.isAscending) {
                            cb.asc(root.get<Any>(sortField))
                        } else {
                            cb.desc(root.get<Any>(sortField))
                        }
                    q.orderBy(order)
                }
            }

            if (predicates.isEmpty()) {
                null
            } else {
                cb.and(*predicates.toTypedArray())
            }
        }
}
