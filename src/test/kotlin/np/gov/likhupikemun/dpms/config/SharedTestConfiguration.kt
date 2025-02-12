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
import org.testcontainers.containers.wait.strategy.Wait
import java.time.Duration

@TestConfiguration
class SharedTestConfiguration {
    private val minioContainer =
        MinioContainer(
            credentials = MinioContainer.CredentialsProvider(ACCESS_KEY, SECRET_KEY),
        ).apply {
            withStartupTimeout(Duration.ofSeconds(30))
            waitingFor(Wait.forHttp("/minio/health/ready").forPort(9000))
        }

    @PostConstruct
    fun startContainer() {
        minioContainer.start()
        Thread.sleep(2000) // Increased delay to ensure container is ready
    }

    @PreDestroy
    fun stopContainer() {
        minioContainer.stop()
    }

    @Bean
    @Primary
    fun minioClient(): MinioClient {
        val client =
            MinioClient
                .builder()
                .endpoint("http://${minioContainer.hostAddress}")
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build()

        try {
            // Test connection and create bucket
            if (!client.bucketExists(
                    io.minio.BucketExistsArgs
                        .builder()
                        .bucket(TEST_BUCKET)
                        .build(),
                )
            ) {
                client.makeBucket(
                    io.minio.MakeBucketArgs
                        .builder()
                        .bucket(TEST_BUCKET)
                        .region("us-east-1")
                        .build(),
                )
            }
            // Verify connection works
            client.listBuckets()
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize MinIO client and bucket", e)
        }

        return client
    }

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
