package np.gov.mofaga.imis.common.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Order
import jakarta.persistence.criteria.Root
import org.hibernate.Session
import org.hibernate.query.Query
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
abstract class BaseHibernateRepository(
    protected val entityManager: EntityManager,
) {
    protected val session: Session
        get() = entityManager.unwrap(Session::class.java)

    protected fun <T> executeQuery(
        queryString: String,
        entityClass: Class<T>,
        parameters: Map<Int, Any?> = emptyMap(),
        pageable: Pageable? = null,
        orders: List<Order>? = null,
    ): List<T> {
        val query: Query<T> = session.createQuery(queryString, entityClass)
        parameters.forEach { (position, value) ->
            query.setParameter(position, value)
        }

        pageable?.let {
            query.setFirstResult(it.offset.toInt())
            query.setMaxResults(it.pageSize)

            if (it.sort.isSorted) {
                val criteriaBuilder = session.criteriaBuilder
                val criteriaQuery = criteriaBuilder.createQuery(entityClass)
                val root = criteriaQuery.from(entityClass)

                val orders = addOrderBy(criteriaBuilder, root, it.sort)
                orders.forEach { order ->
                    criteriaQuery.orderBy(order)
                }
            }
        }

        return query.resultList
    }

    protected fun <T> executePagedQuery(
        queryString: String,
        countQuery: String,
        entityClass: Class<T>,
        parameters: Map<Int, Any?> = emptyMap(),
        pageable: Pageable,
        orders: List<Order>? = null,
    ): Page<T> {
        val results = executeQuery(queryString, entityClass, parameters, pageable, orders)
        val total = executeCount(countQuery, parameters)
        return PageImpl(results, pageable, total)
    }

    private fun executeCount(
        countQuery: String,
        parameters: Map<Int, Any?> = emptyMap(),
    ): Long =
        session.createQuery(countQuery, Long::class.java).run {
            parameters.forEach { (position, value) ->
                setParameter(position, value)
            }
            singleResult
        }

    protected fun <T> addOrderBy(
        builder: CriteriaBuilder,
        root: Root<T>,
        sort: Sort,
    ): List<Order> =
        sort
            .stream()
            .map { order ->
                if (order.isAscending) {
                    builder.asc(root.get<Any>(order.property))
                } else {
                    builder.desc(root.get<Any>(order.property))
                }
            }.toList()
}
