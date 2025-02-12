package np.gov.likhupikemun.dpms.config

import io.minio.MinioClient
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import np.gov.likhupikemun.dpms.shared.storage.MinioContainer
import np.gov.likhupikemun.dpms.shared.storage.MinioProperties
import np.gov.likhupikemun.dpms.shared.storage.StorageProperties
import np.gov.likhupikemun.dpms.shared.storage.StorageService
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class SharedTestConfiguration {
    private val minioContainer =
        MinioContainer(
            credentials = MinioContainer.CredentialsProvider(ACCESS_KEY, SECRET_KEY),
        )

    @PostConstruct
    fun startContainer() {
        minioContainer.start()
    }

    @PreDestroy
    fun stopContainer() {
        minioContainer.stop()
    }

    @Bean
    @Primary
    fun minioClient(): MinioClient =
        MinioClient
            .builder()
            .endpoint("http://${minioContainer.hostAddress}")
            .credentials(ACCESS_KEY, SECRET_KEY)
            .build()

    @Bean
    @Primary
    fun storageProperties(): StorageProperties =
        StorageProperties(
            minio =
                MinioProperties(
                    endpoint = "http://${minioContainer.hostAddress}",
                    accessKey = ACCESS_KEY,
                    secretKey = SECRET_KEY,
                    bucket = TEST_BUCKET,
                ),
        )

    @Bean
    @Primary
    fun storageService(
        minioClient: MinioClient,
        storageProperties: StorageProperties,
    ): StorageService = StorageService(minioClient, storageProperties)

    companion object {
        const val ACCESS_KEY = "minioaccess"
        const val SECRET_KEY = "miniosecret"
        const val TEST_BUCKET = "test-bucket"
    }
}
