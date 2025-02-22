package np.gov.mofaga.imis

import np.gov.mofaga.imis.config.JpaConfig
import np.gov.mofaga.imis.shared.config.*
import np.gov.mofaga.imis.shared.security.config.SecurityConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(
    JacksonConfig::class,
    JwtAuthenticationFilter::class,
    SecurityConfig::class,
    WebSecurityConfig::class,
    ApplicationConfig::class,
    RestConfig::class,
    MetricsConfig::class,
    JpaConfig::class,
)
@ComponentScan(
    basePackages = ["np.gov.mofaga.imis"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = [
                "np.gov.mofaga.imis.shared.config.*",
                "np.gov.mofaga.imis.shared.exception.GlobalExceptionHandler",
            ],
        ),
    ],
)
@EnableConfigurationProperties(MinioProperties::class)
class ImisApiApplication

fun main(args: Array<String>) {
    runApplication<ImisApiApplication>(*args)
}
