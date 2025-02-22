package np.gov.mofaga.imis.common.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Selection
import org.hibernate.Session

interface CriteriaRepository<T> {
    val entityManager: EntityManager
    val session: Session get() = entityManager.unwrap(Session::class.java)

    fun getCriteriaBuilder(): CriteriaBuilder = session.criteriaBuilder

    fun <R> createTupleQuery(vararg selections: Selection<*>): List<Tuple> {
        val cb = getCriteriaBuilder()
        val query = cb.createTupleQuery()
        val root = query.from(getEntityClass())

        query.multiselect(*selections)

        return session.createQuery(query).resultList
    }

    fun <R> createSelectionQuery(vararg paths: Path<*>): List<Tuple> {
        val cb = getCriteriaBuilder()
        val query = cb.createTupleQuery()
        val root = query.from(getEntityClass())

        query.multiselect(paths.toList())

        return session.createQuery(query).resultList
    }

    fun getEntityClass(): Class<T>
}
