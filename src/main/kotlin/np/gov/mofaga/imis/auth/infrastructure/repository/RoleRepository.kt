package np.gov.mofaga.imis.auth.infrastructure.repository

import np.gov.mofaga.imis.auth.domain.Role
import np.gov.mofaga.imis.auth.domain.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByRoleType(roleType: RoleType): Role?

    fun findByRoleTypeIn(roleTypes: Collection<RoleType>): Set<Role>
}
