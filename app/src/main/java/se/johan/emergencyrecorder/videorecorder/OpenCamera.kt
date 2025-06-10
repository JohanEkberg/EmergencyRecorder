package se.johan.emergencyrecorder.videorecorder

import android.content.Context

class OpenCamera(private val repository: VideoRecorderRepository) {
    @Throws(VideoRecorderException::class)
    operator fun invoke(context: Context, onOpened: () -> Unit) : Boolean {
        return try {
            repository.openCamera(context, onOpened)
        } catch(e: Exception) {
            throw VideoRecorderException("Failed to open camera, exception: ${e.message}")
        }
    }
}