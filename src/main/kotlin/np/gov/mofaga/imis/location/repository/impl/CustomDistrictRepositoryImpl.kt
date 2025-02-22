package np.gov.mofaga.imis.location.repository.impl

import jakarta.persistence.EntityManager
import np.gov.mofaga.imis.common.repository.BaseHibernateRepository
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.District_
import np.gov.mofaga.imis.location.domain.Province_
import np.gov.mofaga.imis.location.repository.CustomDistrictRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

class CustomDistrictRepositoryImpl(
    entityManager: EntityManager,
) : BaseHibernateRepository(entityManager),
    CustomDistrictRepository {
    override fun findByProvinceCode(provinceCode: String): List<District> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(District::class.java)
        val district = query.from(District::class.java)
        val province = district.join(District_.province)

        query.where(
            cb.equal(province.get(Province_.code), provinceCode),
        )

        return entityManager.createQuery(query).resultList
    }

    override fun findByCode(code: String): Optional<District> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(District::class.java)
        val district = query.from(District::class.java)

        query.where(
            cb.equal(district.get(District_.code), code),
        )

        return entityManager
            .createQuery(query)
            .resultList
            .firstOrNull()
            ?.let { Optional.of(it) }
            ?: Optional.empty()
    }

    override fun findByCodeIgnoreCase(code: String): Optional<District> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(District::class.java)
        val district = query.from(District::class.java)

        query.where(
            cb.equal(cb.lower(district.get(District_.code)), code.lowercase()),
        )

        return entityManager
            .createQuery(query)
            .resultList
            .firstOrNull()
            ?.let { Optional.of(it) }
            ?: Optional.empty()
    }

    override fun existsByCode(code: String): Boolean {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val district = query.from(District::class.java)

        query
            .select(cb.count(district))
            .where(cb.equal(cb.lower(district.get(District_.code)), code.lowercase()))

        return entityManager.createQuery(query).singleResult > 0
    }

    override fun existsByCodeAndProvince(
        code: String,
        provinceCode: String,
    ): Boolean {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val district = query.from(District::class.java)
        val province = district.join(District_.province)

        query
            .select(cb.count(district))
            .where(
                cb.and(
                    cb.equal(cb.lower(district.get(District_.code)), code.lowercase()),
                    cb.equal(cb.lower(province.get(Province_.code)), provinceCode.lowercase()),
                ),
            )

        return entityManager.createQuery(query).singleResult > 0
    }

    override fun findLargeDistricts(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: Pageable,
    ): Page<District> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(District::class.java)
        val district = query.from(District::class.java)

        // Create predicates
        val populationPredicate = cb.greaterThanOrEqualTo(district.get(District_.population), minPopulation)
        val areaPredicate = cb.greaterThanOrEqualTo(district.get(District_.area), minArea)
        query.where(cb.and(populationPredicate, areaPredicate))

        // Handle sorting - fixed orderBy implementation
        if (pageable.sort.isSorted) {
            val orders = mutableListOf<jakarta.persistence.criteria.Order>()
            pageable.sort.forEach { order ->
                val path = district.get<Any>(order.property)
                orders.add(
                    if (order.isAscending) cb.asc(path) else cb.desc(path),
                )
            }
            query.orderBy(orders)
        }

        // Execute query with pagination
        val typedQuery =
            entityManager
                .createQuery(query)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize)

        // Count query
        val countQuery = cb.createQuery(Long::class.java)
        val countRoot = countQuery.from(District::class.java)
        countQuery.select(cb.count(countRoot))
        countQuery.where(
            cb.and(
                cb.greaterThanOrEqualTo(countRoot.get(District_.population), minPopulation),
                cb.greaterThanOrEqualTo(countRoot.get(District_.area), minArea),
            ),
        )

        val total = entityManager.createQuery(countQuery).singleResult

        return PageImpl(typedQuery.resultList, pageable, total)
    }

    override fun findNearbyDistricts(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
        pageable: Pageable,
    ): Page<District> {
        val query = """
            from District d 
            where exists (
                select 1 from Municipality m 
                where m.district = d 
                and st_distance_sphere(
                    point(m.longitude, m.latitude),
                    point(?1, ?2)
                ) <= ?3
            )
        """
        val countQuery = """
            select count(distinct d) 
            from District d 
            where exists (
                select 1 from Municipality m 
                where m.district = d 
                and st_distance_sphere(
                    point(m.longitude, m.latitude),
                    point(?1, ?2)
                ) <= ?3
            )
        """
        val params =
            mapOf(
                1 to longitude,
                2 to latitude,
                3 to radiusInMeters,
            )
        return executePagedQuery(query, countQuery, District::class.java, params, pageable, null)
    }
}
