package np.gov.mofaga.imis.shared.service

import np.gov.mofaga.imis.auth.domain.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService {
    fun getCurrentUser(): User = SecurityContextHolder.getContext().authentication.principal as User
}
