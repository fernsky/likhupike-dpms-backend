package np.gov.likhupikemun.dpms.family.config

import jakarta.servlet.MultipartConfigElement
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.servlet.MultipartConfigFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.unit.DataSize

@Configuration
class FamilyConfig {
    @Bean
    fun multipartConfigElement(): MultipartConfigElement {
        val factory = MultipartConfigFactory()
        factory.setMaxFileSize(DataSize.ofMegabytes(1))
        factory.setMaxRequestSize(DataSize.ofMegabytes(5))
        return factory.createMultipartConfig()
    }
}

@ConfigurationProperties(prefix = "dpms.family")
data class FamilyProperties(
    val photo: PhotoProperties = PhotoProperties(),
)

data class PhotoProperties(
    val maxFileSize: Long = 1048576, // 1MB
    val allowedTypes: List<String> = listOf("image/jpeg", "image/png"),
)
