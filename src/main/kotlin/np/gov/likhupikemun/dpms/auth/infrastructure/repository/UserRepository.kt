package np.gov.likhupikemun.dpms.auth.infrastructure.repository

import np.gov.likhupikemun.dpms.auth.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface UserRepository :
    JpaRepository<User, String>,
    JpaSpecificationExecutor<User> {
    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun findByIsApprovedFalseAndWardNumberOrWardNumberIsNull(
        wardNumber: Int?,
        pageable: Pageable,
    ): Page<User>
}
