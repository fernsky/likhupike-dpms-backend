package np.gov.likhupikemun.dpms.auth.infrastructure.repository

import np.gov.likhupikemun.dpms.auth.api.dto.request.UserSearchCriteria
import np.gov.likhupikemun.dpms.auth.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    @Query(
        """
        SELECT u FROM User u 
        WHERE u.isApproved = false 
        AND (:wardNumber IS NULL OR u.wardNumber = :wardNumber)
    """,
    )
    fun findPendingUsers(
        wardNumber: Int?,
        pageable: Pageable,
    ): Page<User>

    @Query(
        """
        SELECT u FROM User u 
        WHERE (:#{#criteria.wardNumberFrom} IS NULL OR u.wardNumber >= :#{#criteria.wardNumberFrom})
        AND (:#{#criteria.wardNumberTo} IS NULL OR u.wardNumber <= :#{#criteria.wardNumberTo})
        AND (:#{#criteria.searchTerm} IS NULL 
            OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :#{#criteria.searchTerm}, '%'))
            OR LOWER(u.fullNameNepali) LIKE LOWER(CONCAT('%', :#{#criteria.searchTerm}, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :#{#criteria.searchTerm}, '%')))
        AND (:#{#criteria.roles} IS NULL OR u.roles IN :#{#criteria.roles})
        AND (:#{#criteria.officePosts} IS NULL OR u.officePost IN :#{#criteria.officePosts})
        AND (:#{#criteria.isApproved} IS NULL OR u.isApproved = :#{#criteria.isApproved})
        AND (:#{#criteria.isMunicipalityLevel} IS NULL OR u.isMunicipalityLevel = :#{#criteria.isMunicipalityLevel})
        ORDER BY
        CASE :#{#criteria.sortBy} 
            WHEN 'FULL_NAME' THEN u.fullName
            WHEN 'FULL_NAME_NEPALI' THEN u.fullNameNepali
            WHEN 'WARD_NUMBER' THEN CAST(u.wardNumber AS string)
            WHEN 'OFFICE_POST' THEN u.officePost
            WHEN 'EMAIL' THEN u.email
            WHEN 'APPROVAL_STATUS' THEN CAST(u.isApproved AS string)
            ELSE u.createdAt
        END :#{#criteria.sortDirection}
    """,
    )
    fun searchUsers(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<User>
}
