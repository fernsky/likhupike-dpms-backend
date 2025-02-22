package np.gov.mofaga.imis.auth.infrastructure.repository.specifications

import jakarta.persistence.criteria.JoinType
import np.gov.mofaga.imis.auth.api.dto.request.UserSearchCriteria
import np.gov.mofaga.imis.auth.domain.Role
import np.gov.mofaga.imis.auth.domain.User
import org.springframework.data.jpa.domain.Specification

object UserSpecifications {
    fun fromCriteria(criteria: UserSearchCriteria): Specification<User> =
        Specification
            .where(withWardNumberRange(criteria))
            .and(withSearchTerm(criteria))
            .and(withRoles(criteria))
            .and(withOfficePosts(criteria))
            .and(withApprovalStatus(criteria))
            .and(withMunicipalityLevel(criteria))
            .and(withCreatedDateRange(criteria))
            .and(withDateOfBirthRange(criteria))

    private fun withWardNumberRange(criteria: UserSearchCriteria) =
        Specification<User> { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            criteria.wardNumberFrom?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get("wardNumber"), it))
            }
            criteria.wardNumberTo?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get("wardNumber"), it))
            }

            if (predicates.isEmpty()) null else cb.and(*predicates.toTypedArray())
        }

    private fun withSearchTerm(criteria: UserSearchCriteria) =
        Specification<User> { root, _, cb ->
            criteria.searchTerm?.let { term ->
                val pattern = "%${term.lowercase()}%"
                cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("fullNameNepali")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                )
            }
        }

    private fun withRoles(criteria: UserSearchCriteria) =
        Specification<User> { root, query, _ ->
            criteria.roles?.let { roles ->
                if (roles.isNotEmpty()) {
                    query?.distinct(true)
                    val rolesJoin = root.join<User, Role>("roles", JoinType.INNER)
                    rolesJoin.get<Any>("roleType").`in`(roles)
                } else {
                    null
                }
            }
        }

    private fun withOfficePosts(criteria: UserSearchCriteria) =
        Specification<User> { root, _, _ ->
            criteria.officePosts?.let { posts ->
                if (posts.isNotEmpty()) {
                    root.get<String>("officePost").`in`(posts)
                } else {
                    null
                }
            }
        }

    private fun withApprovalStatus(criteria: UserSearchCriteria) =
        Specification<User> { root, _, cb ->
            criteria.isApproved?.let { isApproved ->
                cb.equal(root.get<Boolean>("isApproved"), isApproved)
            }
        }

    private fun withMunicipalityLevel(criteria: UserSearchCriteria) =
        Specification<User> { root, _, cb ->
            criteria.isMunicipalityLevel?.let { isMunicipalityLevel ->
                cb.equal(root.get<Boolean>("isMunicipalityLevel"), isMunicipalityLevel)
            }
        }

    private fun withCreatedDateRange(criteria: UserSearchCriteria) =
        Specification<User> { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            criteria.createdAfter?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), it.atStartOfDay()))
            }
            criteria.createdBefore?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), it.plusDays(1).atStartOfDay()))
            }

            if (predicates.isEmpty()) null else cb.and(*predicates.toTypedArray())
        }

    private fun withDateOfBirthRange(criteria: UserSearchCriteria) =
        Specification<User> { root, _, cb ->
            val predicates = mutableListOf<jakarta.persistence.criteria.Predicate>()

            criteria.dateOfBirthFrom?.let {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOfBirth"), it))
            }
            criteria.dateOfBirthTo?.let {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOfBirth"), it))
            }

            if (predicates.isEmpty()) null else cb.and(*predicates.toTypedArray())
        }
}
