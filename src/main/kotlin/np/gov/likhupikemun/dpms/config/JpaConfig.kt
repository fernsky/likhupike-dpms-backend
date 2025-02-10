package np.gov.likhupikemun.dpms.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableSpringDataWebSupport
@EnableJpaRepositories(
    basePackages = ["np.gov.likhupikemun.dpms"],
    repositoryImplementationPostfix = "Impl"
)
class JpaConfig
