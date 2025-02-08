package np.gov.likhupikemun.dpms.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfig {
    class CustomPostgreSQLContainer : PostgreSQLContainer<Nothing>("postgis/postgis:16-3.4")

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> =
        CustomPostgreSQLContainer().apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            withCommand("postgres", "-c", "password_encryption=scram-sha-256")
            start()
        }
}
