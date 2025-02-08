package np.gov.likhupikemun.dpms.auth.infrastructure.repository

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
        WHERE (:wardNumber IS NULL OR u.wardNumber = :wardNumber)
        AND (:searchTerm IS NULL 
            OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
    """,
    )
    fun searchUsers(
        wardNumber: Int?,
        searchTerm: String?,
        pageable: Pageable,
    ): Page<User>
}
