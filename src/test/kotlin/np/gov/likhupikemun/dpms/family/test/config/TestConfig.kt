package np.gov.likhupikemun.dpms.family.test.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver

@TestConfiguration
class TestConfig {
    @Bean
    fun multipartResolver(): MultipartResolver =
        CommonsMultipartResolver().apply {
            setMaxUploadSize(5242880) // 5MB
        }
}
