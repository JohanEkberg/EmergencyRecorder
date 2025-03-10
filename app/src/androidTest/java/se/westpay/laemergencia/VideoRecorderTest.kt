package se.westpay.laemergencia

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoRecorderTest {

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )

    @Test
    fun openBackCameraTest() {
        runBlocking {
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val videoRecorder = VideoRecorder()
                assertTrue(videoRecorder.openCamera(context))
            }
        }
    }

    @Test
    fun prepareRecordingTest() {
        runBlocking {
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                val videoRecorder = VideoRecorder()
                assertTrue(videoRecorder.prepareRecording(context) {
                    Log.i(TAG, "Recording started or stopped = ${it}")
                } != null)
            }
        }
    }

    @Test
    fun performRecordingTest() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            var videoRecorder: VideoRecorder? = null
            var success: Boolean = false

            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                success = false
                videoRecorder = VideoRecorder()
                videoRecorder?.let {
                    assertTrue(it.openCamera(context))
                    assertTrue(it.prepareRecording(context) {
                        Log.i(TAG, "Recording started or stopped = $it")
                        if (it) {
                            success = true
                        }
                    } != null)
                } ?: assertTrue(false)
            }

            delay(5000)
            videoRecorder?.let {
                it.startRecordingSession(context)
                delay(3000)
                it.stopRecording()
                it.release()
                delay(1000)
                assertTrue(success)
            } ?: assertTrue(false)
        }
    }
}