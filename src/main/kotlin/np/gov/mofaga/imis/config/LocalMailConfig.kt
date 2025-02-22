package np.gov.mofaga.imis.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties

@Configuration
@Profile("local")
class LocalMailConfig {
    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "localhost"
        mailSender.port = 1025

        val props = Properties()
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "false"
        props["mail.smtp.starttls.enable"] = "false"
        props["mail.debug"] = "true"

        mailSender.javaMailProperties = props
        return mailSender
    }
}
