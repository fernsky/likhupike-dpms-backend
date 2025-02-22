package np.gov.mofaga.imis

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestImisApplication {
    fun main(args: Array<String>) {
        fromApplication<ImisApiApplication>().with(TestImisApplication::class).run(*args)
    }
}
