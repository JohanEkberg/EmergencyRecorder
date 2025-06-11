package se.johan.emergencyrecorder.viewmodels

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.SurfaceView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.johan.emergencyrecorder.TAG
import se.johan.emergencyrecorder.locator.LocatorUseCases
import se.johan.emergencyrecorder.service.googledrive.ServiceUseCases
import se.johan.emergencyrecorder.videorecorder.VideoRecorderUseCases
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val videoRecorderUseCases: VideoRecorderUseCases,
    private val locatorUseCases: LocatorUseCases,
    private val serviceUseCases: ServiceUseCases

) : ViewModel() {
    private val _isPermissionGranted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    private val _isCameraOpen: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isCameraOpen: StateFlow<Boolean> = _isCameraOpen

    private val _isRecording: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isRecording: StateFlow<Boolean> = _isRecording

    private var _videoFile: File? = null
    private var _hasPendingUpload: Boolean = false

    fun updatePermissionStatus(granted: Boolean) {
        if (_isPermissionGranted.value != granted) {
            Log.i(TAG, "Permission granted: $granted")
            _isPermissionGranted.value = granted
        }
    }

    fun deleteFilesOnHost(context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val driveService = serviceUseCases.getDriveService(context)
                driveService?.let {
                    serviceUseCases.deleteAllFiles(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete files on host, exception: ${e.message}")
            e.printStackTrace()
        }
    }

    fun openCamera(context: Context) {
        try {
            if (!videoRecorderUseCases.openCamera(context) { _isCameraOpen.value = true }) {
                Log.e(TAG, "Failed to open camera")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open camera, exception: ${e.message}")
            e.printStackTrace()
        }
    }

    fun startCamera(context: Context, surfaceView: SurfaceView) {
        _videoFile = videoRecorderUseCases.prepareRecording(context) {
            _isRecording.value = it
            val fileSize = _videoFile?.length() ?: 0
            if (!_isRecording.value && fileSize > 0) {
                manageRecordedFile(context)
            }
        }
        videoRecorderUseCases.startRecordingSession(context, surfaceView)
    }

    fun toggleButton(context: Context, surfaceView: SurfaceView) {
        if (_isRecording.value) {
            videoRecorderUseCases.stopRecording()
            manageRecordedFile(context)
        } else {
            val locator = locatorUseCases
            locator.getLocation(context) { location ->
                location?.let {
                    manageLocationData(context = context, location = it)
                } ?: Log.e(TAG, "Invalid location data!")
            }

            _videoFile = videoRecorderUseCases.prepareRecording(context) {
                _isRecording.value = it
                val fileSize = _videoFile?.length() ?: 0
                if (!_isRecording.value && fileSize > 0) {
                    manageRecordedFile(context)
                }
            }
            videoRecorderUseCases.startRecordingSession(context, surfaceView)
        }
    }

    fun getCurrentLocation(context: Context) {
        val locator = locatorUseCases
        locator.getLocation(context) { location ->
            location?.let {
                manageLocationData(context = context, location = it)
            } ?: Log.e(TAG, "Invalid location data!")
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
                val driveService = serviceUseCases.getDriveService(context)
                driveService?.let { service ->
                    Log.d(TAG, "Create location file on server...")
                    val fileId =
                        serviceUseCases.createTextFile(service, "${latitude},${longitude}")
                    fileId?.let { id ->
                        Log.d(TAG, "Send location file to registered user...")
                        serviceUseCases.shareFileWithUser(
                            service,
                            id,
                            "johan.ekberg.666@gmail.com"
                        )
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
                val driveService = serviceUseCases.getDriveService(context)
                driveService?.let { service ->
                    Log.d(TAG, "Create video file on server...")
                    val fileId = serviceUseCases.createMediaFile(service, _videoFile)
                    fileId?.let { id ->
                        Log.d(TAG, "Send video file to registered user...")
                        serviceUseCases.shareFileWithUser(
                            service,
                            id,
                            "johan.ekberg.666@gmail.com"
                        )
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