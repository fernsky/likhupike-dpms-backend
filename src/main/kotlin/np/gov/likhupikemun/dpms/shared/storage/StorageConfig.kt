package np.gov.likhupikemun.dpms.shared.storage

import io.minio.MinioClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StorageConfig(
    private val storageProperties: StorageProperties,
) {
    @Bean
    fun minioClient(): MinioClient =
        MinioClient
            .builder()
            .endpoint(storageProperties.minio.endpoint)
            .credentials(storageProperties.minio.accessKey, storageProperties.minio.secretKey)
            .build()
}

@ConfigurationProperties(prefix = "dpms")
data class StorageProperties(
    val minio: MinioProperties,
)

data class MinioProperties(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
)
