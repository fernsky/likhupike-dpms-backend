package np.gov.likhupikemun.dpms.config

import org.mockito.kotlin.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender

@Configuration
@Profile("test")
class TestEmailConfig {
    @Bean
    fun javaMailSender(): JavaMailSender = mock()
}
