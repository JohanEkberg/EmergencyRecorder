package se.johan.emergencyrecorder.service.googledrive

import com.google.api.services.drive.Drive

class ShareFileWithUser(private val repository: GoogleDriveServiceRepository) {
    @Throws(ServiceException::class)
    operator fun invoke(driveService: Drive, fileId: String, userEmail: String) : Boolean {
        return try {
            repository.shareFileWithUser(driveService = driveService, fileId = fileId, userEmail = userEmail)
        } catch(e: Exception) {
            throw ServiceException("Failed to share file with user ${userEmail}, exception: ${e.message}")
        }
    }
}