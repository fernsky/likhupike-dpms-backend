package np.gov.likhupikemun.dpms.common.repository

import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.hibernate.query.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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
        orders: List<Order<Any>>? = null,
    ): List<T> =
        session.createSelectionQuery(queryString, entityClass).run {
            parameters.forEach { (position, value) ->
                setParameter(position, value)
            }
            orders?.forEach { addOrder(it) }
            pageable?.let {
                setFirstResult(it.offset.toInt())
                setMaxResults(it.pageSize)
            }
            resultList
        }

    protected fun <T> executePagedQuery(
        queryString: String,
        countQuery: String,
        entityClass: Class<T>,
        parameters: Map<Int, Any?> = emptyMap(),
        pageable: Pageable,
        orders: List<Order<Any>>? = null,
    ): Page<T> {
        val results = executeQuery(queryString, entityClass, parameters, pageable, orders)
        val total = executeCount(countQuery, parameters)
        return PageImpl(results, pageable, total)
    }

    private fun executeCount(
        countQuery: String,
        parameters: Map<Int, Any?> = emptyMap(),
    ): Long =
        session.createSelectionQuery(countQuery, Long::class.java).run {
            parameters.forEach { (position, value) ->
                setParameter(position, value)
            }
            singleResult
        }
}
