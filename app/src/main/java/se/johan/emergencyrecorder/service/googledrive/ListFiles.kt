package se.johan.emergencyrecorder.service.googledrive

import com.google.api.services.drive.Drive

class ListFiles(private val repository: GoogleDriveServiceRepository) {
    @Throws(ServiceException::class)
    operator fun invoke(driveService: Drive) : List<String> {
        return try {
            repository.listServiceAccountFiles(driveService = driveService)
        } catch(e: Exception) {
            throw ServiceException("Failed to list files, exception: ${e.message}")
        }
    }
}