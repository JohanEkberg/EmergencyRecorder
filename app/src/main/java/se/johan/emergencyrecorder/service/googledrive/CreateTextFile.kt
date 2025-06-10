package se.johan.emergencyrecorder.service.googledrive

import com.google.api.services.drive.Drive

class CreateTextFile(private val repository: GoogleDriveServiceRepository) {
    @Throws(ServiceException::class)
    operator fun invoke(driveService: Drive, content: String) : String? {
        return try {
            repository.createTextFile(driveService = driveService, content = content)
        } catch(e: Exception) {
            throw ServiceException("Failed to create text file, exception: ${e.message}")
        }
    }
}