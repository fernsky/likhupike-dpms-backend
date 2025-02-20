package np.gov.likhupikemun.dpms.location.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import np.gov.likhupikemun.dpms.common.repository.BaseHibernateRepository
import np.gov.likhupikemun.dpms.location.domain.Province
import np.gov.likhupikemun.dpms.location.domain.Province_
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
class ProvinceRepositoryImpl(
    entityManager: EntityManager,
) : BaseHibernateRepository(entityManager),
    CustomProvinceRepository {
    override fun findByCodeIgnoreCase(code: String): Optional<Province> {
        val cb = session.criteriaBuilder
        val criteriaQuery = cb.createQuery(Province::class.java)
        val province = criteriaQuery.from(Province::class.java)

        criteriaQuery.where(
            cb.equal(
                cb.lower(province.get(Province_.code)),
                code.lowercase(),
            ),
        )

        return session
            .createQuery(criteriaQuery)
            .resultList
            .firstOrNull()
            .let { Optional.ofNullable(it) }
    }

    override fun findLargeProvinces(
        minArea: BigDecimal,
        minPopulation: Long,
        pageable: Pageable,
    ): Page<Province> {
        val cb = session.criteriaBuilder

        // Create main query
        val criteriaQuery = cb.createQuery(Province::class.java)
        val province = criteriaQuery.from(Province::class.java)

        criteriaQuery.where(
            cb.and(
                cb.greaterThanOrEqualTo(province.get(Province_.area), minArea),
                cb.greaterThanOrEqualTo(province.get(Province_.population), minPopulation),
            ),
        )

        // Add sorting
        val orders =
            pageable.sort.map { order ->
                if (order.isAscending) {
                    cb.asc(province.get<Any>(order.property))
                } else {
                    cb.desc(province.get<Any>(order.property))
                }
            }
        criteriaQuery.orderBy(orders)

        // Execute main query with pagination
        val results =
            session
                .createQuery(criteriaQuery)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize)
                .resultList

        // Count total results
        val total =
            executeCountQuery(cb) { root ->
                cb.and(
                    cb.greaterThanOrEqualTo(root.get(Province_.area), minArea),
                    cb.greaterThanOrEqualTo(root.get(Province_.population), minPopulation),
                    cb.isTrue(root.get(Province_.isActive)),
                )
            }

        return PageImpl(results, pageable, total)
    }

    override fun existsByCode(code: String): Boolean {
        val cb = session.criteriaBuilder
        val criteriaQuery = cb.createQuery(Boolean::class.java)
        val province = criteriaQuery.from(Province::class.java)

        val predicates =
            mutableListOf(
                cb.equal(
                    cb.lower(province.get(Province_.code)),
                    code.lowercase(),
                ),
            )

        criteriaQuery
            .select(cb.literal(true))
            .where(*predicates.toTypedArray())

        return session
            .createQuery(criteriaQuery)
            .setMaxResults(1)
            .resultList
            .isNotEmpty()
    }

    private fun executeCountQuery(
        cb: CriteriaBuilder,
        wherePredicate: (jakarta.persistence.criteria.Root<Province>) -> jakarta.persistence.criteria.Predicate,
    ): Long {
        val criteriaQuery = cb.createQuery(Long::class.java)
        val root = criteriaQuery.from(Province::class.java)

        criteriaQuery
            .select(cb.count(root))
            .where(wherePredicate(root))

        return session.createQuery(criteriaQuery).singleResult
    }
}
