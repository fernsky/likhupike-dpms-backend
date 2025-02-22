package np.gov.mofaga.imis

import np.gov.mofaga.imis.config.RedisConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(
    properties = [
        "spring.main.allow-bean-definition-overriding=true",
        "management.metrics.enable.all=true",
    ],
)
@ComponentScan(
    basePackages = ["np.gov.mofaga.imis"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [RedisConfig::class],
        ),
    ],
)
@DirtiesContext
class DpmsApplicationTests
