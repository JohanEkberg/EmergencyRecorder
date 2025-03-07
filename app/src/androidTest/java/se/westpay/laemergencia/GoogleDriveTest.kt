package se.westpay.laemergencia

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class GoogleDriveTest {
    @Test
    fun getGoogleDriveServiceTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val driveService = GoogleDriveService.getDriveService(context)
        assertTrue(driveService != null)
    }

    @Test
    fun createFileOnGoogleDriveTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val driveService = GoogleDriveService.getDriveService(context)
        driveService?.let {
            val id = GoogleDriveService.createDriveFile(it)
            assertTrue(id != null)
        } ?: assertTrue(false)
    }

    @Test
    fun writeToFileOnGoogleDriveTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val driveService = GoogleDriveService.getDriveService(context)
        driveService?.let { drive ->
            val id = GoogleDriveService.createDriveFile(drive)
            id?.let { fileId ->
                val data = byteArrayOf(
                    'A'.code.toByte(),
                    'B'.code.toByte(),
                    'C'.code.toByte(),
                    'D'.code.toByte(),
                    'E'.code.toByte(),
                    'F'.code.toByte()
                )
                assertTrue(GoogleDriveService.uploadChunk(drive, fileId, data))
            } ?: assertTrue(false)
        } ?: assertTrue(false)
    }

    @Test
    fun listFilesOnGoogleDriveTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val driveService = GoogleDriveService.getDriveService(context)
        driveService?.let { drive ->
            val id = GoogleDriveService.createDriveFile(drive)
            id?.let { fileId ->
                val data = byteArrayOf(
                    'A'.code.toByte(),
                    'B'.code.toByte(),
                    'C'.code.toByte(),
                    'D'.code.toByte(),
                    'E'.code.toByte(),
                    'F'.code.toByte()
                )
                if (GoogleDriveService.uploadChunk(drive, fileId, data)) {
                    assertTrue(GoogleDriveService.listServiceAccountFiles(drive))
                } else {
                    assertTrue(false)
                }
            } ?: assertTrue(false)
        } ?: assertTrue(false)
    }

    @Test
    fun shareFilesWithUserTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val driveService = GoogleDriveService.getDriveService(context)
        driveService?.let { drive ->
            val id = GoogleDriveService.createDriveFile(drive)
            id?.let { fileId ->
                assertTrue(
                    GoogleDriveService.shareFileWithUser(drive, fileId, "johan.ekberg.666@gmail.com")
                )
            } ?: assertTrue(false)
        } ?: assertTrue(false)
    }
}