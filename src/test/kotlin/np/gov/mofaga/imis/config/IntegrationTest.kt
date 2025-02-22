package np.gov.mofaga.imis.config

import np.gov.mofaga.imis.config.TestSecurityConfig
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@WebMvcTest
@Import(TestSecurityConfig::class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
annotation class IntegrationTest
