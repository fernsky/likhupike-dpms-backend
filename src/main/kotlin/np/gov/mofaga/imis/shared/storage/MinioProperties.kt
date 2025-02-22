package np.gov.mofaga.imis.shared.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dpms.minio")
data class MinioProperties(
    var endpoint: String = "",
    var accessKey: String = "",
    var secretKey: String = "",
    var bucket: String = "",
)
