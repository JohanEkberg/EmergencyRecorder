package se.johan.emergencyrecorder.service.googledrive

import android.content.Context
import com.google.api.services.drive.Drive

class GetDriveService(private val repository: GoogleDriveServiceRepository) {
    @Throws(ServiceException::class)
    operator fun invoke(context: Context) : Drive? {
        return try {
            repository.getDriveService(context)
        } catch(e: Exception) {
            throw ServiceException("Failed to get drive service, exception: ${e.message}")
        }
    }
}