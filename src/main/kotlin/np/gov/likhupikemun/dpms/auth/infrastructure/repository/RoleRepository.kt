package np.gov.likhupikemun.dpms.auth.infrastructure.repository

import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RoleRepository : JpaRepository<Role, String> {
    @Query("SELECT r FROM Role r WHERE r.roleType IN :roleTypes")
    fun findByNameIn(roleTypes: Collection<RoleType>): List<Role>
}
