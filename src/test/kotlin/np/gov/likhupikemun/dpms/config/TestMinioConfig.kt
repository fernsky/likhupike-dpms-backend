package np.gov.likhupikemun.dpms.config

import org.springframework.boot.test.context.TestConfiguration

@TestConfiguration
class SharedTestConfiguration {
    // private val minioContainer =
    //     MinioContainer(
    //         credentials = MinioContainer.CredentialsProvider(ACCESS_KEY, SECRET_KEY),
    //     ).apply {
    //         withStartupTimeout(Duration.ofSeconds(30))
    //         // Use the mapped port in the health check
    //         waitingFor(Wait.forHttp("/minio/health/ready"))
    //     }

    // @PostConstruct
    // fun startContainer() {
    //     minioContainer.start()
    //     Thread.sleep(2000) // Increased delay to ensure container is ready

    //     // Update system properties to override application-test.yml
    //     System.setProperty("dpms.minio.endpoint", "http://${minioContainer.hostAddress}")
    //     System.setProperty("dpms.minio.accessKey", ACCESS_KEY)
    //     System.setProperty("dpms.minio.secretKey", SECRET_KEY)
    //     System.setProperty("dpms.minio.bucket", TEST_BUCKET)
    // }

    // @PreDestroy
    // fun stopContainer() {
    //     minioContainer.stop()

    //     // Clean up system properties
    //     System.clearProperty("dpms.minio.endpoint")
    //     System.clearProperty("dpms.minio.accessKey")
    //     System.clearProperty("dpms.minio.secretKey")
    //     System.clearProperty("dpms.minio.bucket")
    // }

    // @Bean
    // @Primary
    // fun minioClient(): MinioClient {
    //     val endpoint = "http://${minioContainer.hostAddress}"
    //     println("Connecting to MinIO at: $endpoint") // Add logging for debugging

    //     val client =
    //         MinioClient
    //             .builder()
    //             .endpoint(endpoint)
    //             .credentials(ACCESS_KEY, SECRET_KEY)
    //             .build()

    //     try {
    //         // Test connection and create bucket
    //         if (!client.bucketExists(
    //                 io.minio.BucketExistsArgs
    //                     .builder()
    //                     .bucket(TEST_BUCKET)
    //                     .build(),
    //             )
    //         ) {
    //             client.makeBucket(
    //                 io.minio.MakeBucketArgs
    //                     .builder()
    //                     .bucket(TEST_BUCKET)
    //                     .region("us-east-1")
    //                     .build(),
    //             )
    //         }
    //         // Verify connection works
    //         client.listBuckets()
    //         println("Successfully connected to MinIO and verified bucket") // Add logging for debugging
    //     } catch (e: Exception) {
    //         println("Failed to initialize MinIO client: ${e.message}") // Add logging for debugging
    //         throw IllegalStateException("Failed to initialize MinIO client and bucket", e)
    //     }

    //     return client
    // }

    // @Bean
    // @Primary
    // fun storageProperties(): StorageProperties =
    //     StorageProperties(
    //         minio =
    //             MinioProperties(
    //                 endpoint = "http://${minioContainer.hostAddress}",
    //                 accessKey = ACCESS_KEY,
    //                 secretKey = SECRET_KEY,
    //                 bucket = TEST_BUCKET,
    //             ),
    //     )

    // @Bean
    // @Primary
    // fun storageService(
    //     minioClient: MinioClient,
    //     storageProperties: StorageProperties,
    // ): StorageService = StorageService(minioClient, storageProperties)

    // companion object {
    //     const val ACCESS_KEY = "minioaccess"
    //     const val SECRET_KEY = "miniosecret"
    //     const val TEST_BUCKET = "test-bucket"
    // }
}
