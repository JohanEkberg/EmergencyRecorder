package se.johan.emergencyrecorder.service.googledrive

import com.google.api.services.drive.Drive

class DeleteFile(private val repository: GoogleDriveServiceRepository) {
    @Throws(ServiceException::class)
    operator fun invoke(driveService: Drive, fileId: String) {
        return try {
            repository.deleteFile(driveService = driveService, fileId = fileId)
        } catch(e: Exception) {
            throw ServiceException("Failed to delete file, exception: ${e.message}")
        }
    }
}