package se.johan.emergencyrecorder.service.googledrive

import com.google.api.services.drive.Drive
import java.io.File

class CreateMediaFile(private val repository: GoogleDriveServiceRepository) {
    @Throws(ServiceException::class)
    operator fun invoke(driveService: Drive, fileToBeCreated: File? = null) : String? {
        return try {
            repository.createMediaFile(driveService = driveService, fileToBeCreated = fileToBeCreated)
        } catch(e: Exception) {
            throw ServiceException("Failed to create media file, exception: ${e.message}")
        }
    }
}