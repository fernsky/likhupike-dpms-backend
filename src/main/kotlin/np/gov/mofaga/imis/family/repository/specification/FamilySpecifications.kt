package np.gov.mofaga.imis.family.repository.specification

import jakarta.persistence.criteria.Predicate
import np.gov.mofaga.imis.family.api.dto.request.FamilySearchCriteria
import np.gov.mofaga.imis.family.domain.Family
import org.springframework.data.jpa.domain.Specification

object FamilySpecifications {
    fun withSearchCriteria(criteria: FamilySearchCriteria): Specification<Family> =
        Specification { root, _, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()

            criteria.headOfFamily?.let { name ->
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("headOfFamily")),
                        "%${name.lowercase()}%",
                    ),
                )
            }

            criteria.wardNumber?.let { ward ->
                predicates.add(criteriaBuilder.equal(root.get<Int>("wardNumber"), ward))
            }

            if (predicates.isEmpty()) {
                criteriaBuilder.conjunction()
            } else {
                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }
}
