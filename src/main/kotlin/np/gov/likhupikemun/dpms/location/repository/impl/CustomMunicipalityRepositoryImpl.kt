package np.gov.likhupikemun.dpms.location.repository.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import np.gov.likhupikemun.dpms.common.repository.BaseHibernateRepository
import np.gov.likhupikemun.dpms.location.domain.*
import np.gov.likhupikemun.dpms.location.repository.CustomMunicipalityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.*

class CustomMunicipalityRepositoryImpl(
    entityManager: EntityManager,
) : BaseHibernateRepository(entityManager),
    CustomMunicipalityRepository {
    override fun findByDistrictCode(districtCode: String): List<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Municipality::class.java)
        val municipality = query.from(Municipality::class.java)
        val district = municipality.join(Municipality_.district)

        query.where(
            cb.equal(district.get(District_.code), districtCode),
        )

        return entityManager.createQuery(query).resultList
    }

    override fun findByCodeAndDistrictCode(
        code: String,
        districtCode: String,
    ): Optional<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Municipality::class.java)
        val municipality = query.from(Municipality::class.java)
        val district = municipality.join(Municipality_.district)

        query.where(
            cb.and(
                cb.equal(municipality.get(Municipality_.code), code),
                cb.equal(district.get(District_.code), districtCode),
            ),
        )

        return entityManager
            .createQuery(query)
            .resultList
            .firstOrNull()
            .let { Optional.ofNullable(it) }
    }

    override fun findNearby(
        latitude: BigDecimal,
        longitude: BigDecimal,
        radiusInMeters: Double,
        pageable: Pageable,
    ): Page<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createTupleQuery()
        val municipality = query.from(Municipality::class.java)

        // Create spatial distance calculation using native SQL function
        val distanceFunction =
            cb.function(
                "ST_Distance_Sphere",
                Double::class.java,
                cb.function("point", Any::class.java, municipality.get(Municipality_.longitude), municipality.get(Municipality_.latitude)),
                cb.function("point", Any::class.java, cb.literal(longitude), cb.literal(latitude)),
            )

        query
            .multiselect(
                municipality.get(Municipality_.name),
                municipality.get(Municipality_.code),
                distanceFunction.alias("distance"),
            ).where(
                cb.le(distanceFunction, radiusInMeters),
            ).orderBy(
                cb.asc(distanceFunction),
            )

        val results =
            entityManager
                .createQuery(query)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize)
                .resultList
                .map { tuple: Tuple -> mapTupleToMunicipality(tuple) }

        val total =
            executeCountQuery(cb) { root ->
                cb.le(
                    cb.function(
                        "ST_Distance_Sphere",
                        Double::class.java,
                        cb.function("point", Any::class.java, root.get(Municipality_.longitude), root.get(Municipality_.latitude)),
                        cb.function("point", Any::class.java, cb.literal(longitude), cb.literal(latitude)),
                    ),
                    radiusInMeters,
                )
            }

        return PageImpl(results, pageable, total)
    }

    override fun findByTypeAndDistrict(
        type: MunicipalityType,
        districtCode: String,
    ): List<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Municipality::class.java)
        val municipality = query.from(Municipality::class.java)
        val district = municipality.join(Municipality_.district)

        query.where(
            cb.and(
                cb.equal(municipality.get(Municipality_.type), type),
                cb.equal(district.get(District_.code), districtCode),
            ),
        )

        return entityManager.createQuery(query).resultList
    }

    override fun findLargeMunicipalities(
        minPopulation: Long,
        minArea: BigDecimal,
        pageable: Pageable,
    ): Page<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Municipality::class.java)
        val municipality = query.from(Municipality::class.java)

        val predicates =
            listOf(
                cb.greaterThanOrEqualTo(municipality.get(Municipality_.population), minPopulation),
                cb.greaterThanOrEqualTo(municipality.get(Municipality_.area), minArea),
            )

        query.where(*predicates.toTypedArray())

        // Add sorting
        val orders =
            pageable.sort
                .map { order ->
                    if (order.isAscending) {
                        cb.asc(municipality.get<Any>(order.property))
                    } else {
                        cb.desc(municipality.get<Any>(order.property))
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
        val countRoot = countQuery.from(Municipality::class.java)
        countQuery.select(cb.count(countRoot))
        countQuery.where(*predicates.toTypedArray())

        val total = entityManager.createQuery(countQuery).singleResult

        return PageImpl(typedQuery.resultList, pageable, total)
    }

    override fun existsByCodeAndDistrict(
        code: String,
        districtCode: String,
    ): Boolean {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Boolean::class.java)
        val municipality = query.from(Municipality::class.java)
        val district = municipality.join(Municipality_.district)

        val predicates =
            mutableListOf(
                cb.equal(municipality.get(Municipality_.code), code),
                cb.equal(district.get(District_.code), districtCode),
            )

        query
            .select(cb.literal(true))
            .where(*predicates.toTypedArray())

        return entityManager
            .createQuery(query)
            .setMaxResults(1)
            .resultList
            .isNotEmpty()
    }

    override fun countByTypeAndDistrict(districtCode: String): Map<MunicipalityType, Long> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createTupleQuery()
        val municipality = query.from(Municipality::class.java)
        val district = municipality.join(Municipality_.district)

        query
            .multiselect(
                municipality.get(Municipality_.type),
                cb.count(municipality),
            ).where(
                cb.equal(district.get(District_.code), districtCode),
            ).groupBy(
                municipality.get(Municipality_.type),
            )

        return entityManager
            .createQuery(query)
            .resultList
            .associate { tuple: Tuple ->
                tuple.get(0, MunicipalityType::class.java) to tuple.get(1, Long::class.java)
            }
    }

    override fun getTotalPopulationByDistrict(districtCode: String): Long? {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val municipality = query.from(Municipality::class.java)
        val district = municipality.join(Municipality_.district)

        query
            .select(cb.sum(municipality.get(Municipality_.population)))
            .where(
                cb.and(
                    cb.equal(district.get(District_.code), districtCode),
                ),
            )

        return entityManager.createQuery(query).singleResult
    }

    override fun findByMinimumWards(
        minWards: Int,
        pageable: Pageable,
    ): Page<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Municipality::class.java)
        val municipality = query.from(Municipality::class.java)

        query.where(
            cb.greaterThanOrEqualTo(municipality.get(Municipality_.totalWards), minWards),
        )

        // Add sorting
        val orders =
            pageable.sort
                .map { order ->
                    if (order.isAscending) {
                        cb.asc(municipality.get<Any>(order.property))
                    } else {
                        cb.desc(municipality.get<Any>(order.property))
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
        val countRoot = countQuery.from(Municipality::class.java)
        countQuery.select(cb.count(countRoot))
        countQuery.where(
            cb.greaterThanOrEqualTo(countRoot.get(Municipality_.totalWards), minWards),
        )

        val total = entityManager.createQuery(countQuery).singleResult

        return PageImpl(typedQuery.resultList, pageable, total)
    }

    override fun findByCodeIgnoreCase(code: String): Optional<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Municipality::class.java)
        val municipality = query.from(Municipality::class.java)

        query.where(
            cb.equal(cb.lower(municipality.get(Municipality_.code)), code.lowercase()),
        )

        return entityManager
            .createQuery(query)
            .resultList
            .firstOrNull()
            .let { Optional.ofNullable(it) }
    }

    override fun existsByCodeIgnoreCase(code: String): Boolean {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Boolean::class.java)
        val municipality = query.from(Municipality::class.java)

        query
            .select(cb.literal(true))
            .where(
                cb.equal(cb.lower(municipality.get(Municipality_.code)), code.lowercase()),
            )

        return entityManager
            .createQuery(query)
            .setMaxResults(1)
            .resultList
            .isNotEmpty()
    }

    override fun findByType(type: MunicipalityType): List<Municipality> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Municipality::class.java)
        val municipality = query.from(Municipality::class.java)

        query.where(
            cb.equal(municipality.get(Municipality_.type), type),
        )

        return entityManager.createQuery(query).resultList
    }

    private fun mapTupleToMunicipality(tuple: Tuple): Municipality =
        Municipality().apply {
            name = tuple.get(1) as String
            code = tuple.get(2) as String
            // Map other fields based on the query selection
            tuple.elements.getOrNull(3)?.let {
                when (it.alias) {
                    "population" -> population = it.javaType as Long
                    "area" -> area = it.javaType as BigDecimal
                    "totalWards" -> totalWards = it.javaType as Int
                }
            }
        }

    private fun executeCountQuery(
        cb: jakarta.persistence.criteria.CriteriaBuilder,
        wherePredicate: (jakarta.persistence.criteria.Root<Municipality>) -> jakarta.persistence.criteria.Predicate,
    ): Long {
        val countQuery = cb.createQuery(Long::class.java)
        val root = countQuery.from(Municipality::class.java)

        countQuery
            .select(cb.count(root))
            .where(wherePredicate(root))

        return entityManager.createQuery(countQuery).singleResult
    }
}
