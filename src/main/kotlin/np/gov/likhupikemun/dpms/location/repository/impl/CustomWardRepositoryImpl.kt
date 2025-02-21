package np.gov.likhupikemun.dpms.location.repository.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import np.gov.likhupikemun.dpms.common.repository.BaseHibernateRepository
import np.gov.likhupikemun.dpms.location.domain.*
import np.gov.likhupikemun.dpms.location.repository.CustomWardRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.*

class CustomWardRepositoryImpl(
    entityManager: EntityManager,
) : BaseHibernateRepository(entityManager),
    CustomWardRepository {
    override fun findByMunicipalityCode(municipalityCode: String): List<Ward> {
        val cb = session.criteriaBuilder
        val query = cb.createQuery(Ward::class.java)
        val ward = query.from(Ward::class.java)
        val municipality = ward.join(Ward_.municipality)

        query.where(
            cb.equal(municipality.get(Municipality_.code), municipalityCode),
        )

        return session.createQuery(query).resultList
    }

    override fun findByDistrictCode(districtCode: String): List<Ward> {
        val cb = session.criteriaBuilder
        val query = cb.createQuery(Ward::class.java)
        val ward = query.from(Ward::class.java)
        val municipality = ward.join(Ward_.municipality)
        val district = municipality.join(Municipality_.district)

        query.where(
            cb.equal(district.get(District_.code), districtCode),
        )

        return session.createQuery(query).resultList
    }

    override fun findByProvinceCode(provinceCode: String): List<Ward> {
        val cb = session.criteriaBuilder
        val query = cb.createQuery(Ward::class.java)
        val ward = query.from(Ward::class.java)
        val municipality = ward.join(Ward_.municipality)
        val district = municipality.join(Municipality_.district)
        val province = district.join(District_.province)

        query.where(
            cb.equal(province.get(Province_.code), provinceCode),
        )

        return session.createQuery(query).resultList
    }

    override fun findByWardNumberAndMunicipalityCode(
        wardNumber: Int,
        municipalityCode: String,
    ): Optional<Ward> {
        val cb = session.criteriaBuilder
        val query = cb.createQuery(Ward::class.java)
        val ward = query.from(Ward::class.java)
        val municipality = ward.join(Ward_.municipality)

        query.where(
            cb.and(
                cb.equal(ward.get(Ward_.wardNumber), wardNumber),
                cb.equal(municipality.get(Municipality_.code), municipalityCode),
            ),
        )

        return session
            .createQuery(query)
            .resultList
            .firstOrNull()
            .let { Optional.ofNullable(it) }
    }

    override fun findByWardNumberRange(
        municipalityCode: String,
        fromWard: Int,
        toWard: Int,
    ): List<Ward> {
        val cb = session.criteriaBuilder
        val query = cb.createQuery(Ward::class.java)
        val ward = query.from(Ward::class.java)
        val municipality = ward.join(Ward_.municipality)

        query.where(
            cb.and(
                cb.equal(municipality.get(Municipality_.code), municipalityCode),
                cb.between(ward.get(Ward_.wardNumber), fromWard, toWard),
            ),
        )

        return session.createQuery(query).resultList
    }

    override fun existsByWardNumberAndMunicipality(
        wardNumber: Int,
        municipalityCode: String,
    ): Boolean {
        val cb = session.criteriaBuilder
        val query = cb.createQuery(Boolean::class.java)
        val ward = query.from(Ward::class.java)
        val municipality = ward.join(Ward_.municipality)

        query
            .select(cb.literal(true))
            .where(
                cb.and(
                    cb.equal(ward.get(Ward_.wardNumber), wardNumber),
                    cb.equal(municipality.get(Municipality_.code), municipalityCode),
                ),
            )

        return session
            .createQuery(query)
            .setMaxResults(1)
            .resultList
            .isNotEmpty()
    }

    override fun findByPopulationRange(
        minPopulation: Long,
        maxPopulation: Long,
        pageable: Pageable,
    ): Page<Ward> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Ward::class.java)
        val ward = query.from(Ward::class.java)

        query.where(
            cb.between(ward.get(Ward_.population), minPopulation, maxPopulation),
        )

        // Add sorting
        val orders =
            pageable.sort
                .map { order ->
                    if (order.isAscending) {
                        cb.asc(ward.get<Any>(order.property))
                    } else {
                        cb.desc(ward.get<Any>(order.property))
                    }
                }.toList()

        query.orderBy(orders)

        val typedQuery =
            entityManager
                .createQuery(query)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize)

        // Count query
        val countQuery = cb.createQuery(Long::class.java)
        val countRoot = countQuery.from(Ward::class.java)
        countQuery.select(cb.count(countRoot))
        countQuery.where(
            cb.between(countRoot.get(Ward_.population), minPopulation, maxPopulation),
        )

        val total = entityManager.createQuery(countQuery).singleResult

        return PageImpl(typedQuery.resultList, pageable, total)
    }

    override fun countByMunicipalityCode(municipalityCode: String): Int {
        val cb = session.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val ward = query.from(Ward::class.java)
        val municipality = ward.join(Ward_.municipality)

        query
            .select(cb.count(ward))
            .where(
                cb.equal(municipality.get(Municipality_.code), municipalityCode),
            )

        return session.createQuery(query).singleResult.toInt()
    }

    private fun mapTupleToWard(tuple: Tuple): Ward =
        Ward().apply {
            wardNumber = tuple.get(0) as Int
            population = tuple.get(1) as Long
            municipality =
                Municipality().apply {
                    code = tuple.get(2) as String
                }
        }

    private fun executeCountQuery(
        cb: jakarta.persistence.criteria.CriteriaBuilder,
        wherePredicate: (jakarta.persistence.criteria.Root<Ward>) -> jakarta.persistence.criteria.Predicate,
    ): Long {
        val countQuery = cb.createQuery(Long::class.java)
        val root = countQuery.from(Ward::class.java)

        countQuery
            .select(cb.count(root))
            .where(wherePredicate(root))

        return session.createQuery(countQuery).singleResult
    }
}
