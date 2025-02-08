package np.gov.likhupikemun.dpms.shared.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class FileService {
    fun storeFile(file: MultipartFile): String {
        // Implement file storage logic
        return "files/${UUID.randomUUID()}-${file.originalFilename}"
    }

    fun deleteFile(path: String) {
        // Implement file deletion logic
    }
}
