package np.gov.likhupikemun.dpms.shared.storage

import io.minio.MinioClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class StorageTestConfig {
    @Bean
    @Primary
    fun storageService(
        minioClient: MinioClient, // This will be injected from SharedTestConfiguration
        storageProperties: StorageProperties,
    ): StorageService = StorageService(minioClient, storageProperties)
}
