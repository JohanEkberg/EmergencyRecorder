package se.westpay.laemergencia

import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import android.view.SurfaceView
import androidx.core.app.ActivityCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class VideoRecorder : IVideoRecorder {
    private var _mediaRecorder: MediaRecorder? = null
    private lateinit var _cameraDevice: CameraDevice
    private lateinit var _cameraManager: CameraManager
    private lateinit var _cameraId: String
    private var _videoFile: File? = null
    private lateinit var _stateOfRecorder: (Boolean) -> Unit

    override fun openCamera(context: Context) : Boolean {
        return try {
            Log.d(TAG, "Open camera")
            _cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
            // Get the correct camera id
            for (id in _cameraManager.cameraIdList) {
                val characteristics = _cameraManager.getCameraCharacteristics(id)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    _cameraId = id
                    Log.i(TAG, "Has camera id = ${_cameraId}")
                    break
                }
            }

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Camera permission not granted!")
                return false
            }

            // Open camera
            _cameraManager.openCamera(_cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    Log.i(TAG, "Has camera device")
                    _cameraDevice = camera
                }
                override fun onDisconnected(camera: CameraDevice) { camera.close() }
                override fun onError(camera: CameraDevice, error: Int) { camera.close() }
            }, null)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open camera, exception: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    override fun prepareRecording(context: Context, stateOfRecorder: (Boolean) -> Unit) : File? {
        Log.i(TAG, "Setting up video recorder")
        return try {
            _stateOfRecorder = stateOfRecorder
            _videoFile = createVideoFile(context)

            _mediaRecorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setVideoSource(MediaRecorder.VideoSource.SURFACE) // Use Surface source
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(_videoFile!!.absolutePath)
                setVideoSize(1920, 1080)
                setVideoFrameRate(30)
                setOrientationHint(90)
                setMaxFileSize(10 * 1024 * 1024)
                // Set on info listener to keep track on max file reached
                setOnInfoListener { _, what, _ ->
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                        Log.i(TAG, "Max file size reached, stopping recording...")
                        stopRecording()
                    }
                }
                prepare() // Prepare before starting session
            }
            _videoFile
        } catch(e: Exception) {
            Log.e(TAG, "Failed to start recording, exception: ${e.message}")
            null
        }
    }

    private fun createVideoFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "VID_$timeStamp.mp4"
        return File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)
    }

    override fun startRecordingSession(context: Context, surfaceView: SurfaceView) {
        try {
            Log.d(TAG, "Starting recording session...")

            val previewSurface = surfaceView.holder.surface
            val recordingSurface = _mediaRecorder!!.surface

            val captureRequestBuilder = _cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
                addTarget(previewSurface)
                addTarget(recordingSurface) // Add recording surface
                set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
            }

            val sessionConfiguration = SessionConfiguration(
                SessionConfiguration.SESSION_REGULAR, // Type of session
                listOf(OutputConfiguration(previewSurface), OutputConfiguration(recordingSurface)), // Output surfaces
                Executors.newSingleThreadExecutor(),  // Executor to run the callback
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        session.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                        startRecording(context)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("Camera2Helper", "Failed to configure camera session")
                    }
                }
            )
            _cameraDevice.createCaptureSession(sessionConfiguration)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording session, exception: ${e.message}")
        }
    }

    private fun startRecording(context: Context) {
        try {
            _mediaRecorder?.let {
                Log.d(TAG, "Start recording...")
                it.start()
                _stateOfRecorder(true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording, exception: ${e.message}")
            stopRecording()
        }
    }

    override fun stopRecording() {
        Log.i(TAG, "Stop recording")
        try {
            _stateOfRecorder(false)
            _mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            _mediaRecorder = null
        } catch(e: Exception) {
            Log.e(TAG, "Failed to stop recording, exception: ${e.message}")
        }
    }

    override fun release() {
        if (::_cameraDevice.isInitialized) {
            _cameraDevice.close()
        }
    }

//    private fun setMaxFileReached() {
//        _mediaRecorder?.setOnInfoListener { _, what, _ ->
//            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
//                Log.i(TAG, "Max file size reached, stopping recording...")
//                stopRecording()
//            }
//        }
//    }
}