package np.gov.likhupikemun.dpms.location.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import np.gov.likhupikemun.dpms.common.repository.BaseHibernateRepository
import np.gov.likhupikemun.dpms.common.repository.CriteriaRepository
import np.gov.likhupikemun.dpms.location.domain.District
import np.gov.likhupikemun.dpms.location.domain.District_
import np.gov.likhupikemun.dpms.location.domain.Province_
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
class DistrictRepositoryImpl(
    entityManager: EntityManager,
) : BaseHibernateRepository(entityManager),
    CustomDistrictRepository,
    CriteriaRepository<District> {
    override fun getEntityClass(): Class<District> = District::class.java

    override fun findByProvinceCode(provinceCode: String): List<District> {
        val cb = getCriteriaBuilder()
        val query = cb.createQuery(District::class.java)
        val district = query.from(District::class.java)
        val province = district.join(District_.province)

        query.where(
            cb.equal(province.get(Province_.code), provinceCode),
        )

        return session.createQuery(query).resultList
    }

    override fun findByCode(code: String): Optional<District> {
        val cb = getCriteriaBuilder()
        val query = cb.createQuery(District::class.java)
        val district = query.from(District::class.java)
        val province = district.join(District_.province)

        query.where(
            cb.and(
                cb.equal(district.get(District_.code), code),
            ),
        )

        return session
            .createQuery(query)
            .resultList
            .firstOrNull()
            .let { Optional.ofNullable(it) }
    }

    override fun findLargeDistricts(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: Pageable,
    ): Page<District> {
        val cb = getCriteriaBuilder()

        // Main query
        val query = cb.createTupleQuery()
        val district = query.from(District::class.java)

        val predicates =
            listOf(
                cb.greaterThanOrEqualTo(district.get(District_.population), minPopulation),
                cb.greaterThanOrEqualTo(district.get(District_.area), minArea),
                cb.isTrue(district.get(District_.isActive)),
            )

        query
            .multiselect(
                district.get(District_.id),
                district.get(District_.name),
                district.get(District_.code),
                district.get(District_.population),
                district.get(District_.area),
            ).where(*predicates.toTypedArray())

        // Add sorting
        val orders =
            pageable.sort.map { order ->
                if (order.isAscending) {
                    cb.asc(district.get<Any>(order.property))
                } else {
                    cb.desc(district.get<Any>(order.property))
                }
            }
        query.orderBy(orders)

        // Execute query with pagination
        val results =
            session
                .createQuery(query)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize)
                .resultList
                .map { tuple: Tuple -> mapTupleToDistrict(tuple) }

        // Count query
        val total =
            executeCountQuery(cb) { root ->
                cb.and(*predicates.toTypedArray())
            }

        return PageImpl(results, pageable, total)
    }

    override fun existsByCodeAndProvince(
        code: String,
        provinceCode: UUID,
    ): Boolean {
        val cb = getCriteriaBuilder()
        val query = cb.createQuery(Boolean::class.java)
        val district = query.from(District::class.java)
        val province = district.join(District_.province)

        val predicates =
            mutableListOf(
                cb.equal(district.get(District_.code), code),
                cb.equal(province.get(Province_.code), provinceCode),
            )

        query
            .select(cb.literal(true))
            .where(*predicates.toTypedArray())

        return session
            .createQuery(query)
            .setMaxResults(1)
            .resultList
            .isNotEmpty()
    }

    override fun findByMinimumMunicipalities(
        minMunicipalities: Int,
        pageable: Pageable,
    ): Page<District> {
        val cb = getCriteriaBuilder()
        val query = cb.createTupleQuery()
        val district = query.from(District::class.java)
        val municipalities = district.join(District_.municipalities)

        query
            .multiselect(
                district.get(District_.id),
                district.get(District_.name),
                district.get(District_.code),
                cb.count(municipalities).alias("municipalityCount"),
            ).groupBy(district.get(District_.id))
            .having(cb.ge(cb.count(municipalities), minMunicipalities.toLong()))

        val results =
            session
                .createQuery(query)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize)
                .resultList
                .map { tuple: Tuple -> mapTupleToDistrict(tuple) }

        val total =
            executeCountQuery(cb) { root ->
                val municipalityCount = cb.count(root.join(District_.municipalities))
                cb.and(
                    cb.ge(municipalityCount, minMunicipalities.toLong()),
                )
            }

        return PageImpl(results, pageable, total)
    }

    private fun mapTupleToDistrict(tuple: Tuple): District =
        District().apply {
            id = tuple.get(0) as UUID
            name = tuple.get(1) as String
            code = tuple.get(2) as String
            // Map other fields as needed
        }

    private fun executeCountQuery(
        cb: jakarta.persistence.criteria.CriteriaBuilder,
        wherePredicate: (jakarta.persistence.criteria.Root<District>) -> jakarta.persistence.criteria.Predicate,
    ): Long {
        val countQuery = cb.createQuery(Long::class.java)
        val root = countQuery.from(District::class.java)

        countQuery
            .select(cb.count(root))
            .where(wherePredicate(root))

        return session.createQuery(countQuery).singleResult
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
