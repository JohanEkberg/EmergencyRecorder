package se.johan.emergencyrecorder.service.googledrive

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.Permission
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import se.johan.emergencyrecorder.R
import se.johan.emergencyrecorder.TAG
import java.io.File
import java.io.IOException

class GoogleDriveServiceRepository {
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val SERVICE_ACCOUNT_EMAIL = "la-emergencia@laemergencia.iam.gserviceaccount.com"

    fun getDriveService(context: Context): Drive? {
        return try {
            val httpTransport = NetHttpTransport()
            val inputStream = context.resources.openRawResource(R.raw.app_credentials);
            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/drive.file"))

            val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)

            Drive.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                .setApplicationName("DriveUploader")
                .build()
        } catch(e: Exception) {
            Log.e(TAG, "Failed to get google driver service, exception: ${e.message}")
            null
        }
    }

    fun createMediaFile(driveService: Drive, fileToBeCreated: File? = null): String? {
        return try {
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = "LiveRecording.mp4"
                mimeType = "video/mp4"
            }

            val file = if (fileToBeCreated == null) {
                Log.i(TAG, "Create file without content")
                driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute()
            } else if (isFileLessThan3MB(fileToBeCreated)) {
                Log.i(TAG, "Create file with size below 3MB")
                val mediaContent = FileContent("video/mp4", fileToBeCreated)
                driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()
            } else {
                Log.i(TAG, "Create file with size above 3MB")
                val mediaContent = FileContent("video/mp4", fileToBeCreated)
                val insert = driveService.files().create(fileMetadata, mediaContent)
                insert.mediaHttpUploader.isDirectUploadEnabled = false // Enables resumable upload
                insert.mediaHttpUploader.chunkSize = calculateChunkSize(fileToBeCreated.length())
                insert.setFields("id").execute()
            }
            file?.id
        } catch(e: Exception) {
            Log.e(TAG, "Failed to create google driver file, exception: ${e.message}")
            null
        }
    }

    private fun isFileLessThan3MB(file: File) : Boolean {
        return file.length() < (3 * 1024 * 1024) // 3MB in bytes
    }

    private fun calculateChunkSize(fileSize: Long, numChunks: Int = 10): Int {
        var chunkSize = (fileSize / numChunks).toInt()

        // Ensure chunk size is at least 256KB and a multiple of 262144
        val minChunkSize = 256 * 1024 // 256 KB
        chunkSize = ((chunkSize + 262143) / 262144) * 262144 // Round up to the nearest multiple of 262144

        return maxOf(chunkSize, minChunkSize) // Ensure chunk size is at least 256KB
    }

    fun createTextFile(driveService: Drive, content: String) : String? {
        return try {
            val fileMetadata = com.google.api.services.drive.model.File()
            fileMetadata.name = "location.txt"
            fileMetadata.mimeType = "text/plain"

            val fileContent = ByteArrayContent.fromString("text/plain", content)

            val file = driveService.files().create(fileMetadata, fileContent)
                .setFields("id")
                .execute()
            file?.id
        } catch(e: Exception) {
            Log.e(TAG, "Failed to create google driver file, exception: ${e.message}")
            null
        }
    }

    fun shareFileWithUser(driveService: Drive, fileId: String, userEmail: String) : Boolean {
        return try {
            val permission = Permission().apply {
                type = "user"  // Share with a specific user
                role = "reader"  // Read-only access
                emailAddress = userEmail
            }

            driveService.permissions().create(fileId, permission).execute()
            Log.d(TAG, "File shared with $userEmail")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share files to user ${userEmail}, exception: ${e.message}")
            false
        }
    }

    // TODO: Add delete and clear files function!!!

    fun uploadChunk(driveService: Drive, fileId: String, chunkData: ByteArray) : Boolean {
        return try {
            val mediaContent = ByteArrayContent("video/mp4", chunkData)

            driveService.files().update(fileId, null, mediaContent)
                .setFields("id")
                .execute()

            Log.i(TAG, "Chunk uploaded successfully!")
            true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to upload file chunk to google drive, exception: ${e.message}")
            e.printStackTrace()
            false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload file chunk to google drive, exception: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun listServiceAccountFiles(driveService: Drive) : List<String> {
        return try {
            val fileNames = mutableListOf<String>()
            val result = driveService.files().list()
                .setFields("files(id, name, mimeType, createdTime)")
                .execute()

            val files = result.files ?: emptyList()
            for (file in files) {
                Log.d(TAG, "File: ${file.name}, ID: ${file.id}, Type: ${file.mimeType}, Created: ${file.createdTime}")
                file.name?.let { fileNames.add(it) }
            }
            fileNames
        } catch (e: Exception) {
            Log.e(TAG, "Failed to list files on google drive, exception: ${e.message}")
            emptyList<String>()
        }
    }

    fun deleteFile(driveService: Drive, fileId: String) {
        try {
            driveService.files().delete(fileId).execute()
        } catch (e: GoogleJsonResponseException) {
            Log.e(TAG, "Failed to delete file ${fileId} on google drive, exception: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete file ${fileId} on google drive, exception: ${e.message}")
        }
    }

    fun deleteAllFiles(driveService: Drive) {
        try {
            val result = driveService.files().list()
                .setFields("files(id, name, mimeType, createdTime)")
                .execute()
            val files = result.files ?: emptyList()
            for (file in files) {
                Log.d(TAG, "File: ${file.name}, ID: ${file.id}, Type: ${file.mimeType}, Created: ${file.createdTime}")
                deleteFile(driveService, file.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete all files on google drive, exception: ${e.message}")
        }
    }
}