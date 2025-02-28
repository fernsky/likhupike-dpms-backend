package np.gov.mofaga.imis.shared.config

import io.minio.MinioClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class MinioConfig(
    private val minioProperties: MinioProperties,
) {
    @Bean
    fun minioClient(): MinioClient =
        MinioClient
            .builder()
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.accessKey, minioProperties.secretKey)
            .build()
}
