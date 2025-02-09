package np.gov.likhupikemun.dpms

import np.gov.likhupikemun.dpms.shared.config.ApplicationConfig
import np.gov.likhupikemun.dpms.shared.config.JacksonConfig
import np.gov.likhupikemun.dpms.shared.config.JpaConfig
import np.gov.likhupikemun.dpms.shared.config.JwtAuthenticationFilter
import np.gov.likhupikemun.dpms.shared.config.MetricsConfig
import np.gov.likhupikemun.dpms.shared.config.RestConfig
import np.gov.likhupikemun.dpms.shared.config.WebSecurityConfig
import np.gov.likhupikemun.dpms.shared.security.config.SecurityConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
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
    basePackages = ["np.gov.likhupikemun.dpms"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = ["np.gov.likhupikemun.dpms.shared.config.*"],
        ),
    ],
)
class DpmsApiApplication

fun main(args: Array<String>) {
    runApplication<DpmsApiApplication>(*args)
}
