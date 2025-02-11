package np.gov.likhupikemun.dpms.family.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver

@Configuration
class FamilyConfig {
    @Bean
    fun multipartResolver(): MultipartResolver =
        CommonsMultipartResolver().apply {
            setMaxUploadSize(5242880) // 5MB
            setMaxUploadSizePerFile(1048576) // 1MB per file
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
