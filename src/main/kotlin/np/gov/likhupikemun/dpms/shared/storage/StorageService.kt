package np.gov.likhupikemun.dpms.shared.storage

import io.minio.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.util.*

@Service
class StorageService(
    private val minioClient: MinioClient,
    private val storageProperties: StorageProperties,
) {
    fun uploadFile(
        file: MultipartFile,
        path: String,
    ): String {
        val objectName = "$path/${UUID.randomUUID()}-${file.originalFilename}"

        minioClient.putObject(
            PutObjectArgs
                .builder()
                .bucket(storageProperties.minio.bucket)
                .`object`(objectName)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType)
                .build(),
        )

        return objectName
    }

    fun getFile(path: String): InputStream =
        minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(storageProperties.minio.bucket)
                .`object`(path)
                .build(),
        )

    fun deleteFile(path: String) {
        minioClient.removeObject(
            RemoveObjectArgs
                .builder()
                .bucket(storageProperties.minio.bucket)
                .`object`(path)
                .build(),
        )
    }
}
