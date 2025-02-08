package np.gov.likhupikemun.dpms

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestDpmsApplication {
    fun main(args: Array<String>) {
        fromApplication<DpmsApiApplication>().with(TestDpmsApplication::class).run(*args)
    }
}
