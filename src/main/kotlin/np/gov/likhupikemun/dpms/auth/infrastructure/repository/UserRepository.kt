package np.gov.likhupikemun.dpms.auth.infrastructure.repository

import np.gov.likhupikemun.dpms.auth.domain.RoleType
import np.gov.likhupikemun.dpms.auth.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    @Query(
        """
        SELECT u FROM user u 
        WHERE u.isApproved = false 
        AND (:wardNumber IS NULL OR u.wardNumber = :wardNumber)
        """,
    )
    fun findPendingUsers(
        wardNumber: Int?,
        pageable: Pageable,
    ): Page<User>

    @Query(
        value = """
        SELECT DISTINCT u FROM user u 
        WHERE 1=1
        AND (:wardNumberFrom IS NULL OR u.wardNumber >= :wardNumberFrom)
        AND (:wardNumberTo IS NULL OR u.wardNumber <= :wardNumberTo)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
            OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.fullNameNepali) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (COALESCE(:roles, NULL) IS NULL OR SIZE(:roles) = 0 OR u.roles IN (:roles))
        AND (COALESCE(:officePosts, NULL) IS NULL OR SIZE(:officePosts) = 0 OR u.officePost IN (:officePosts))
        AND (:isApproved IS NULL OR u.isApproved = :isApproved)
        AND (:isMunicipalityLevel IS NULL OR u.isMunicipalityLevel = :isMunicipalityLevel)
        ORDER BY u.createdAt DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT u) FROM user u 
        WHERE 1=1
        AND (:wardNumberFrom IS NULL OR u.wardNumber >= :wardNumberFrom)
        AND (:wardNumberTo IS NULL OR u.wardNumber <= :wardNumberTo)
        AND (:searchTerm IS NULL OR :searchTerm = '' 
            OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.fullNameNepali) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (COALESCE(:roles, NULL) IS NULL OR SIZE(:roles) = 0 OR u.roles IN (:roles))
        AND (COALESCE(:officePosts, NULL) IS NULL OR SIZE(:officePosts) = 0 OR u.officePost IN (:officePosts))
        AND (:isApproved IS NULL OR u.isApproved = :isApproved)
        AND (:isMunicipalityLevel IS NULL OR u.isMunicipalityLevel = :isMunicipalityLevel)
        """,
    )
    fun searchUsers(
        @Param("wardNumberFrom") wardNumberFrom: Int?,
        @Param("wardNumberTo") wardNumberTo: Int?,
        @Param("searchTerm") searchTerm: String?,
        @Param("roles") roles: Set<RoleType> = emptySet(),
        @Param("officePosts") officePosts: Set<String> = emptySet(),
        @Param("isApproved") isApproved: Boolean?,
        @Param("isMunicipalityLevel") isMunicipalityLevel: Boolean?,
        pageable: Pageable,
    ): Page<User>
}
