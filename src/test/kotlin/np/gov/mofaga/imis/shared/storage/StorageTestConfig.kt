package np.gov.mofaga.imis.shared.storage

import io.minio.MinioClient
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class StorageTestConfig {
    @Bean
    @Primary
    fun minioClient(): MinioClient = mock()

    @Bean
    @Primary
    fun storageProperties() =
        StorageProperties(
            minio =
                MinioProperties(
                    endpoint = "http://mock-minio:9000",
                    accessKey = "mock-key",
                    secretKey = "mock-secret",
                    bucket = "mock-bucket",
                ),
        )

    @Bean
    @Primary
    fun storageService(
        minioClient: MinioClient,
        storageProperties: StorageProperties,
    ) = StorageService(minioClient, storageProperties)
}
