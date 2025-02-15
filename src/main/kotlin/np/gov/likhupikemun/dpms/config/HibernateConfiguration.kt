package np.gov.likhupikemun.dpms.config

import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import javax.sql.DataSource

@Configuration
class HibernateConfiguration(
    @Autowired private val environment: Environment,
    @Autowired private val dataSource: DataSource,
) {
    fun createMetadataSources(): MetadataSources {
        val properties: MutableMap<String, Any> = mutableMapOf()
        properties["hibernate.dialect"] =
            environment.getProperty("spring.jpa.properties.hibernate.dialect") ?: "org.hibernate.dialect.PostgreSQLDialect"
        properties["hibernate.connection.driver_class"] =
            environment.getProperty("spring.datasource.driver-class-name") ?: "org.postgresql.Driver"
        properties["hibernate.connection.url"] = environment.getProperty("spring.datasource.url") ?: ""
        properties["hibernate.connection.username"] = environment.getProperty("spring.datasource.username") ?: ""
        properties["hibernate.connection.password"] = environment.getProperty("spring.datasource.password") ?: ""
        properties["hibernate.hbm2ddl.auto"] = "none"
        properties["hibernate.physical_naming_strategy"] = "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"

        val registry =
            StandardServiceRegistryBuilder()
                .applySettings(properties)
                .build()

        return MetadataSources(registry)
    }
}
