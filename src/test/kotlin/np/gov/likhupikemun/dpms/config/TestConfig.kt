package np.gov.likhupikemun.dpms.config

import io.minio.MinioClient
import np.gov.likhupikemun.dpms.shared.security.jwt.JwtService
import np.gov.likhupikemun.dpms.shared.storage.StorageService
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDateTime
import java.util.Optional
import java.util.Properties
import javax.sql.DataSource

@TestConfiguration(proxyBeanMethods = false)
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class TestConfig {
    @Bean
    @Primary
    fun mockJwtService(): JwtService = mock()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests { authorize(anyRequest, permitAll) }
        }
        return http.build()
    }

    // @Bean
    // fun defaultTestDataSource(): DataSource {
    //     // Register H2 driver
    //     Class.forName("org.h2.Driver")

    //     // Create datasource matching application-test.yml configuration
    //     val dataSource =
    //         org.springframework.jdbc.datasource.DriverManagerDataSource().apply {
    //             setDriverClassName("org.h2.Driver")
    //             url = "jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
    //             username = "sa"
    //             password = ""
    //         }

    //     return dataSource
    // }

    @Bean
    @Primary
    fun minioClient(): MinioClient = mock()

    @Bean
    @Primary
    fun storageService(): StorageService = mock()

    @Bean
    fun auditingDateTimeProvider() = DateTimeProvider { Optional.of(LocalDateTime.now()) }

    @Bean
    fun auditorProvider(): AuditorAware<String> = AuditorAware { Optional.of("test-user") }

    @Bean
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        // Initialize H2GIS
        val connection = dataSource.connection
        val scriptContent =
            this::class.java.classLoader
                .getResource("schema.sql")
                ?.readText()
        scriptContent?.let {
            connection.createStatement().execute(it)
        }
        connection.close()

        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("np.gov.likhupikemun.dpms")

        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setGenerateDdl(true)
        em.jpaVendorAdapter = vendorAdapter

        val properties = Properties()
        properties["hibernate.hbm2ddl.auto"] = "create-drop"
        properties["hibernate.dialect"] = "org.hibernate.dialect.H2Dialect"
        properties["hibernate.show_sql"] = "true"
        properties["hibernate.format_sql"] = "true"
        properties["hibernate.jdbc.use_get_generated_keys"] = "true"
        // Add spatial function support
        properties["hibernate.dialect.storage_engine"] = "h2gis"
        em.setJpaProperties(properties)

        return em
    }

    @Bean
    fun transactionManager(entityManagerFactory: LocalContainerEntityManagerFactoryBean): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory.`object`
        return transactionManager
    }
}
