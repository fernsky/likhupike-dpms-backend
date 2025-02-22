package np.gov.mofaga.imis.shared.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dpms.minio")
data class MinioProperties(
    val endpoint: String = "http://localhost:9000",
    val accessKey: String = "minioadmin",
    val secretKey: String = "miniopass",
    val bucket: String = "dpms-files",
)
