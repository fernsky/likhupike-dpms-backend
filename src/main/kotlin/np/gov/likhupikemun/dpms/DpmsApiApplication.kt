package np.gov.likhupikemun.dpms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootApplication
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
