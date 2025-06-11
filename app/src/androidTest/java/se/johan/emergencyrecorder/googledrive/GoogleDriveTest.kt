package se.johan.emergencyrecorder.googledrive

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import se.johan.emergencyrecorder.di.AppModule
import se.johan.emergencyrecorder.service.googledrive.ServiceUseCases
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@HiltAndroidTest
@UninstallModules(AppModule::class)
//@RunWith(AndroidJUnit4::class)
class GoogleDriveTest {

    @Inject
    lateinit var serviceUseCases: ServiceUseCases

    @get: Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
        //grantPermissions()
    }

//    private fun grantPermissions() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        val permission = Manifest.permission.INTERNET
//
//        if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//            InstrumentationRegistry.getInstrumentation().uiAutomation
//                .executeShellCommand("pm grant ${context.packageName} $permission")
//                .close() // Important to close the ParcelFileDescriptor
//        }
//    }



    @Test
    fun getGoogleDriveServiceTest() {
        try {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val driveService = serviceUseCases.getDriveService(context)
            Assert.assertTrue(driveService != null)
        } catch(e: Exception) {
            Log.e("getGoogleDriveServiceTest", "Exception: ${e.message}")
        }
    }

    @Test
    fun createFileOnGoogleDriveTest() {
        try {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val driveService = serviceUseCases.getDriveService(context)
            driveService?.let {
                val id = serviceUseCases.createMediaFile(it)
                Assert.assertTrue(id != null)
            } ?: Assert.assertTrue(false)
        } catch(e: Exception) {
            Log.e("createFileOnGoogleDriveTest", "Exception: ${e.message}")
        }
    }

    @Test
    fun writeToFileOnGoogleDriveTest() {
        try {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val driveService = serviceUseCases.getDriveService(context)
            driveService?.let { it ->
                val data = byteArrayOf(
                    'A'.code.toByte(),
                    'B'.code.toByte(),
                    'C'.code.toByte(),
                    'D'.code.toByte(),
                    'E'.code.toByte(),
                    'F'.code.toByte()
                )
                val file = File(context.filesDir, "testfile")
                FileOutputStream(file).use {
                    it.write(data)
                }
                val id = serviceUseCases.createMediaFile(it, file)
                Assert.assertFalse(id.equals("0"))
                id?.let { it2 -> serviceUseCases.deleteFile(it, it2) }
            } ?: Assert.assertTrue(false)
        } catch(e: Exception) {
            Log.e("writeToFileOnGoogleDriveTest", "Exception: ${e.message}")
            Assert.assertTrue(false)
        }
    }

    @Test
    fun listFilesOnGoogleDriveTest() {
        try {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val driveService = serviceUseCases.getDriveService(context)
            driveService?.let { it ->
                val data = byteArrayOf(
                    'A'.code.toByte(),
                    'B'.code.toByte(),
                    'C'.code.toByte(),
                    'D'.code.toByte(),
                    'E'.code.toByte(),
                    'F'.code.toByte()
                )
                val file = File(context.filesDir, "testfile")
                FileOutputStream(file).use {
                    it.write(data)
                }
                val id = serviceUseCases.createMediaFile(it, file)
                Assert.assertFalse(id.equals("0"))
                val files = serviceUseCases.listFiles(it)
                files.forEach {
                    Log.d("listFilesOnGoogleDriveTest", "File: $it")
                }
                id?.let { it2 -> serviceUseCases.deleteFile(it, it2) }
                Assert.assertTrue(files.size != 1)
            } ?: Assert.assertTrue(false)
        } catch(e: Exception) {
            Log.e("listFilesOnGoogleDriveTest", "Exception: ${e.message}")
            Assert.assertTrue(false)
        }
    }

    @Test
    fun shareFilesWithUserTest() {
        try {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val driveService = serviceUseCases.getDriveService(context)
            driveService?.let { it ->
                val id = serviceUseCases.createMediaFile(it)
                id?.let { fileId ->
                    Assert.assertTrue(
                        serviceUseCases.shareFileWithUser(it, fileId, "johan.ekberg.666@gmail.com")
                    )
                } ?: Assert.assertTrue(false)
            } ?: Assert.assertTrue(false)
        } catch (e: Exception) {
            Log.e("shareFilesWithUserTest", "Exception: ${e.message}")
            Assert.assertTrue(false)
        }
    }
}