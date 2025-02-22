package np.gov.mofaga.imis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class JpaAuditingConfig {
    @Bean
    fun auditorProvider(): AuditorAware<String> =
        AuditorAware {
            Optional
                .ofNullable(SecurityContextHolder.getContext())
                .map { it.authentication }
                .map { it.name }
                .or { Optional.of("system") }
        }
}
