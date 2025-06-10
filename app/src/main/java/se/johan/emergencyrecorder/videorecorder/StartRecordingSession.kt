package se.johan.emergencyrecorder.videorecorder

import android.content.Context
import android.view.SurfaceView

class StartRecordingSession(private val repository: VideoRecorderRepository) {
    @Throws(VideoRecorderException::class)
    operator fun invoke(context: Context, surfaceView: SurfaceView) {
        return try {
            repository.startRecordingSession(context, surfaceView)
        } catch(e: Exception) {
            throw VideoRecorderException("Failed to start recording session, exception: ${e.message}")
        }
    }
}