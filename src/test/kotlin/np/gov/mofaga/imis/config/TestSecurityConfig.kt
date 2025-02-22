package np.gov.mofaga.imis.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@TestConfiguration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
class TestSecurityConfig
