package np.gov.likhupikemun.dpms.auth.infrastructure.repository

import np.gov.likhupikemun.dpms.auth.domain.Role
import np.gov.likhupikemun.dpms.auth.domain.RoleType
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, String> {
    fun findByNameIn(names: Collection<RoleType>): List<Role>
}
