package np.gov.likhupikemun.dpms.config

import np.gov.likhupikemun.dpms.shared.security.jwt.JwtService
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration(proxyBeanMethods = false)
@EnableWebSecurity
class TestSecurityConfig {
    @Bean
    @Primary
    fun mockJwtService(): JwtService = mock()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }
        }
        return http.build()
    }
}
