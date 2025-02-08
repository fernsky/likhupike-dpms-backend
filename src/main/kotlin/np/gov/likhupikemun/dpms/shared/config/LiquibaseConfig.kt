package np.gov.likhupikemun.dpms.shared.config

import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class LiquibaseConfig {
    @Bean
    fun liquibase(dataSource: DataSource) =
        SpringLiquibase().apply {
            this.dataSource = dataSource
            changeLog = "classpath:db/changelog/db.changelog-master.yaml"
            contexts = "development,test,production"
        }
}
