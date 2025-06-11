package se.johan.emergencyrecorder.videorecorder

import android.Manifest
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import se.johan.emergencyrecorder.TAG
import se.johan.emergencyrecorder.di.AppModule
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppModule::class)
//@RunWith(AndroidJUnit4::class)
class VideoRecorderTest {

    @Inject
    lateinit var videoRecorderUseCases: VideoRecorderUseCases

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

    @get: Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun openBackCameraTest() {
        runBlocking {
            try {
                var success = false
                val deferredResult  = CompletableDeferred<Boolean>()
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    val context = InstrumentationRegistry.getInstrumentation().targetContext
                    Assert.assertTrue(videoRecorderUseCases.openCamera(context = context) {
                        success = true
                        deferredResult.complete(true)
                    }
                    )
                }
                success = deferredResult.await()
                Assert.assertTrue(success)
            } catch(e: Exception) {
                Log.e("openBackCameraTest", "Exception: ${e.message}")
                Assert.assertTrue(false)
            }
        }
    }

    @Test
    fun prepareRecordingTest() {
        runBlocking {
            try {
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    val context = InstrumentationRegistry.getInstrumentation().targetContext
                    Assert.assertTrue(videoRecorderUseCases.prepareRecording(context) {
                        Log.i(TAG, "Recording started or stopped = ${it}")
                    } != null)
                }
            } catch(e: Exception) {
                Log.e("prepareRecordingTest", "Exception: ${e.message}")
                Assert.assertTrue(false)
            }
        }
    }

    @Test
    fun performRecordingTest() {
        runBlocking {
            try {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                var success: Boolean = false
                val deferredResult  = CompletableDeferred<Boolean>()

                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    success = false
                    videoRecorderUseCases.openCamera(context) {
                        success = true
                        deferredResult.complete(true)
                    }
                }
                success = deferredResult.await()
                videoRecorderUseCases.prepareRecording(context) {
                    deferredResult.complete(true)
                }
                videoRecorderUseCases.startRecordingSession(context)
                success = deferredResult.await()
                videoRecorderUseCases.stopRecording()
                videoRecorderUseCases.release()
                Assert.assertTrue(success)
            } catch(e: Exception) {
                Log.e("prepareRecordingTest", "Exception: ${e.message}")
                Assert.assertTrue(false)
            }
        }
    }
}