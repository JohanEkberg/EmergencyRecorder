package se.westpay.laemergencia

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.SurfaceView
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel : ViewModel() {
    private val _isRecording = mutableStateOf(false)
    var isRecording: State<Boolean> = _isRecording
    private var _videoFile: File? = null
    private val _videoRecorder: VideoRecorder = VideoRecorder()
    private var _hasPendingUpload: Boolean = false

    fun toggleButton(context: Context, surfaceView: SurfaceView) {
        if (_isRecording.value) {
            _videoRecorder.stopRecording()
            manageRecordedFile(context)
        } else {
            val locator = Locator(context)
            locator.getLocation(context) { location ->
                location?.let {
                    manageLocationData(context = context, location = it)
                } ?: Log.e(TAG, "Invalid location data!")
            }

            _videoFile = _videoRecorder.prepareRecording(context) {
                _isRecording.value = it
                val fileSize = _videoFile?.length() ?: 0
                if (!_isRecording.value && fileSize > 0) {
                    manageRecordedFile(context)
                }
            }
            _videoRecorder.startRecordingSession(context, surfaceView)
        }
    }

    fun openCamera(context: Context) {
        try {
            Log.d(TAG, "Open camera")
            _videoRecorder.openCamera(context)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open camera, exception: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun manageLocationData(context: Context, location: Location) {
        Log.i(TAG, "Latitude = ${location.latitude} Longitude = ${location.longitude}")
        viewModelScope.launch(Dispatchers.IO) {
            val success = uploadLocationData(context, location.latitude, location.longitude)
            Log.i(TAG, "Result of upload location file, success = $success")
        }
    }

    private suspend fun uploadLocationData(context: Context, latitude: Double, longitude: Double) : Boolean {
        return try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Prepare upload of location file...")
                val driveService = GoogleDriveService.getDriveService(context)
                driveService?.let { service ->
                    Log.d(TAG, "Create location file on server...")
                    val fileId = GoogleDriveService.createTextFile(service, "${latitude},${longitude}")
                    fileId?.let { id ->
                        Log.d(TAG, "Send location file to registered user...")
                        GoogleDriveService.shareFileWithUser(service, id, "johan.ekberg.666@gmail.com")
                        true
                    } ?: run {
                        Log.e(TAG, "Invalid file id!")
                        false
                    }
                } ?: run {
                    Log.e(TAG, "Invalid Google Drive Service!")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload the location data file, exception: ${e.message}")
            false
        }
    }

    private fun manageRecordedFile(context: Context) {
        Log.i(TAG, "Has pending upload, $_hasPendingUpload")
        if (!_hasPendingUpload) {
            viewModelScope.launch(Dispatchers.IO) {
                _hasPendingUpload = true
                val success = uploadVideo(context)
                Log.i(TAG, "Result of upload the video file, success = $success")
                if (success) {
                    _videoFile?.delete()
                }
                _hasPendingUpload = false
            }
        }
    }

    private suspend fun uploadVideo(context: Context) : Boolean {
        return try {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "Prepare upload of video file...")
                val driveService = GoogleDriveService.getDriveService(context)
                driveService?.let { service ->
                    Log.d(TAG, "Create video file on server...")
                    val fileId = GoogleDriveService.createDriveFile(service, _videoFile)
                    fileId?.let { id ->
                        Log.d(TAG, "Send video file to registered user...")
                        GoogleDriveService.shareFileWithUser(service, id, "johan.ekberg.666@gmail.com")
                        true
                    } ?: run {
                        Log.e(TAG, "Invalid file id!")
                        false
                    }
                } ?: run {
                    Log.e(TAG, "Invalid Google Drive Service!")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload the video file, exception: ${e.message}")
            false
        }
    }
}