package se.johan.emergencyrecorder.videorecorder

import android.content.Context
import java.io.File

class PrepareRecording(private val repository: VideoRecorderRepository) {
    @Throws(VideoRecorderException::class)
    operator fun invoke(context: Context, stateOfRecorder: (Boolean) -> Unit) : File? {
        return try {
            repository.prepareRecording(context, stateOfRecorder)
        } catch(e: Exception) {
            throw VideoRecorderException("Failed to prepare recording, exception: ${e.message}")
        }
    }
}