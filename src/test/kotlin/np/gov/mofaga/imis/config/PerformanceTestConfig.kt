package np.gov.mofaga.imis.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@TestConfiguration
class PerformanceTestConfig {
    @Bean
    @Primary
    fun performanceTestDataSource(): DataSource {
        val config =
            HikariConfig().apply {
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
                username = "sa"
                password = ""
                maximumPoolSize = 50
                minimumIdle = 10
                idleTimeout = 30000
                connectionTimeout = 10000
                maxLifetime = 1800000
                poolName = "PerformanceTestPool"
                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            }
        return HikariDataSource(config)
    }
}
