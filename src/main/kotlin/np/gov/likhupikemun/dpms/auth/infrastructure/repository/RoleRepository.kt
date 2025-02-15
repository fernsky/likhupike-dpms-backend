package np.gov.likhupikemun.dpms.auth.infrastructure.repository

import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByRoleType(roleType: RoleType): Role?

    fun findByRoleTypeIn(roleTypes: Collection<RoleType>): Set<Role>
}
