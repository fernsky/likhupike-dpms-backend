package np.gov.likhupikemun.dpms.shared.service

import np.gov.likhupikemun.dpms.auth.domain.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService {
    fun getCurrentUser(): User = SecurityContextHolder.getContext().authentication.principal as User
}
