package se.johan.emergencyrecorder.videorecorder

class StopRecording(private val repository: VideoRecorderRepository) {
    @Throws(VideoRecorderException::class)
    operator fun invoke() {
        return try {
            repository.stopRecording()
        } catch(e: Exception) {
            throw VideoRecorderException("Failed to stop recording session, exception: ${e.message}")
        }
    }
}