package se.johan.emergencyrecorder.videorecorder

class Release(private val repository: VideoRecorderRepository) {
    @Throws(VideoRecorderException::class)
    operator fun invoke() {
        return try {
            repository.release()
        } catch(e: Exception) {
            throw VideoRecorderException("Failed to release recorder resources, exception: ${e.message}")
        }
    }
}