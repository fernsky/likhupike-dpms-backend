package np.gov.mofaga.imis.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.Properties

@Configuration
class PasswordResetConfig {
    @Value("\${spring.mail.host}")
    private lateinit var mailHost: String

    @Value("\${spring.mail.port}")
    private var mailPort: Int = 0

    @Value("\${spring.mail.username}")
    private lateinit var mailUsername: String

    @Value("\${spring.mail.password}")
    private lateinit var mailPassword: String

    @Bean
    fun mailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = mailHost
        mailSender.port = mailPort
        mailSender.username = mailUsername
        mailSender.password = mailPassword

        val props = Properties()
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "false"

        mailSender.javaMailProperties = props
        return mailSender
    }
}
