package np.gov.likhupikemun.dpms.config

import liquibase.integration.spring.SpringLiquibase
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("!test")
@EnableConfigurationProperties(LiquibaseProperties::class)
class LiquibaseConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase")
    fun liquibaseProperties(): LiquibaseProperties = LiquibaseProperties()

    @Bean
    fun liquibase(dataSource: DataSource): SpringLiquibase =
        SpringLiquibase().apply {
            this.dataSource = dataSource
            val properties = liquibaseProperties()
            changeLog = properties.changeLog
            contexts = properties.contexts?.joinToString(",")
            defaultSchema = properties.defaultSchema
            isDropFirst = properties.isDropFirst
        }
}
