package se.johan.emergencyrecorder.service.googledrive

import com.google.api.services.drive.Drive

class DeleteAllFiles(private val repository: GoogleDriveServiceRepository) {
    @Throws(ServiceException::class)
    operator fun invoke(driveService: Drive) {
        return try {
            repository.deleteAllFiles(driveService = driveService)
        } catch(e: Exception) {
            throw ServiceException("Failed to delete all files, exception: ${e.message}")
        }
    }
}