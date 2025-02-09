package np.gov.likhupikemun.dpms.shared.event

import np.gov.likhupikemun.dpms.auth.domain.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class UserEventPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publishUserRegistered(user: User) {
        eventPublisher.publishEvent(UserRegisteredEvent(user))
    }

    fun publishUserLoggedIn(user: User) {
        eventPublisher.publishEvent(UserLoggedInEvent(user))
    }
}

data class UserRegisteredEvent(
    val user: User,
)

data class UserLoggedInEvent(
    val user: User,
)
