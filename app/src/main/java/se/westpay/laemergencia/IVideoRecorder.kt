package se.westpay.laemergencia

import android.content.Context
import android.view.SurfaceView
import java.io.File

interface IVideoRecorder {
    fun openCamera(context: Context) : Boolean
    fun prepareRecording(context: Context, stateOfRecorder: (Boolean) -> Unit) : File?
    fun startRecordingSession(context: Context)
    fun startRecordingSession(context: Context, surfaceView: SurfaceView)
    fun stopRecording()
    fun release()
}