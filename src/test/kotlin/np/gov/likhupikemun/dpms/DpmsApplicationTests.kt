package np.gov.likhupikemun.dpms

import np.gov.likhupikemun.dpms.config.RedisConfig
import np.gov.likhupikemun.dpms.config.TestConfig
import np.gov.likhupikemun.dpms.config.TestMetricsConfig
import np.gov.likhupikemun.dpms.config.TestRedisConfig
import np.gov.likhupikemun.dpms.config.TestSecurityConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration",
        "management.metrics.enable.all=true",
    ],
)
@ComponentScan(
    basePackages = ["np.gov.likhupikemun.dpms"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [RedisConfig::class],
        ),
    ],
)
@Import(TestConfig::class, TestRedisConfig::class, TestSecurityConfig::class, TestMetricsConfig::class)
@ActiveProfiles("test")
@DirtiesContext
class DpmsApplicationTests
