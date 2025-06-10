package se.johan.emergencyrecorder.videorecorder

data class VideoRecorderUseCases (
    val openCamera: OpenCamera,
    val prepareRecording: PrepareRecording,
    val startRecordingSession: StartRecordingSession,
    val stopRecording: StopRecording,
    val release: Release
)

class VideoRecorderException(
    message: String = "",
    val errorCode: Int = 0
) : Exception(message)