package np.gov.mofaga.imis.location.repository.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import np.gov.mofaga.imis.common.repository.BaseHibernateRepository
import np.gov.mofaga.imis.location.domain.Province
import np.gov.mofaga.imis.location.domain.Province_
import np.gov.mofaga.imis.location.repository.CustomProvinceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

class CustomProvinceRepositoryImpl(
    entityManager: EntityManager,
) : BaseHibernateRepository(entityManager),
    CustomProvinceRepository {
    override fun findByCodeIgnoreCase(code: String): Optional<Province> {
        val cb = entityManager.criteriaBuilder
        val criteriaQuery = cb.createQuery(Province::class.java)
        val province = criteriaQuery.from(Province::class.java)

        criteriaQuery.where(
            cb.equal(
                cb.lower(province.get(Province_.code)),
                code.lowercase(),
            ),
        )

        return entityManager
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
        val cb = entityManager.criteriaBuilder
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
            pageable.sort
                .map { order ->
                    if (order.isAscending) {
                        cb.asc(province.get<Any>(order.property))
                    } else {
                        cb.desc(province.get<Any>(order.property))
                    }
                }.toList()

        criteriaQuery.orderBy(orders)

        val results =
            entityManager
                .createQuery(criteriaQuery)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize)
                .resultList

        val total =
            executeCountQuery(cb) { root ->
                cb.and(
                    cb.greaterThanOrEqualTo(root.get(Province_.area), minArea),
                    cb.greaterThanOrEqualTo(root.get(Province_.population), minPopulation),
                )
            }

        return PageImpl(results, pageable, total)
    }

    override fun existsByCode(code: String): Boolean {
        val cb = entityManager.criteriaBuilder
        val criteriaQuery = cb.createQuery(Long::class.java)
        val province = criteriaQuery.from(Province::class.java)

        criteriaQuery
            .select(cb.count(province))
            .where(
                cb.equal(
                    cb.lower(province.get(Province_.code)),
                    code.lowercase(),
                ),
            )

        return entityManager
            .createQuery(criteriaQuery)
            .singleResult > 0
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

        return entityManager.createQuery(criteriaQuery).singleResult
    }
}
