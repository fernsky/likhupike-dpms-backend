package np.gov.likhupikemun.dpms.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EntityScan(basePackages = ["np.gov.likhupikemun.dpms"])
@EnableJpaRepositories(basePackages = ["np.gov.likhupikemun.dpms"])
class JpaConfig
