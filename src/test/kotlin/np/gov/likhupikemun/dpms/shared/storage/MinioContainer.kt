package np.gov.likhupikemun.dpms.shared.storage

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.utility.Base58
import java.time.Duration

class MinioContainer(
    image: String? = "$DEFAULT_IMAGE:$DEFAULT_TAG",
    credentials: CredentialsProvider? = null,
) : GenericContainer<MinioContainer>(image ?: "$DEFAULT_IMAGE:$DEFAULT_TAG") {
    data class CredentialsProvider(
        val accessKey: String,
        val secretKey: String,
    )

    init {
        withNetworkAliases("minio-" + Base58.randomString(6))
        addExposedPort(DEFAULT_PORT)
        credentials?.let {
            withEnv(MINIO_ACCESS_KEY, it.accessKey)
            withEnv(MINIO_SECRET_KEY, it.secretKey)
        }
        withCommand("server", DEFAULT_STORAGE_DIRECTORY)
        setWaitStrategy(
            HttpWaitStrategy()
                .forPort(DEFAULT_PORT)
                .forPath(HEALTH_ENDPOINT)
                .withStartupTimeout(Duration.ofMinutes(2)),
        )
    }

    val hostAddress: String
        get() = "$containerIpAddress:${getMappedPort(DEFAULT_PORT)}"

    companion object {
        private const val DEFAULT_PORT = 9000
        private const val DEFAULT_IMAGE = "minio/minio"
        private const val DEFAULT_TAG = "edge"
        private const val MINIO_ACCESS_KEY = "MINIO_ACCESS_KEY"
        private const val MINIO_SECRET_KEY = "MINIO_SECRET_KEY"
        private const val DEFAULT_STORAGE_DIRECTORY = "/data"
        private const val HEALTH_ENDPOINT = "/minio/health/ready"
    }
}
