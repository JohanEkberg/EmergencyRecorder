package se.westpay.laemergencia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request required permission
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this@MainActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        } else {
            setContent {
                MyApp()
            }
        }
    }

    @Composable
    fun MyApp() {
        Scaffold(
            topBar = { TopBarWithMenu() }
        ) { paddingValues ->
            // Avoid re-instantiate the view model, make Android to remember the viewModel
            val viewModel = viewModel<MainViewModel>()
            MainScreen(viewModel, modifier = Modifier.padding(paddingValues))
        }
    }

    @Composable
    fun MainScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
        Log.i(TAG, "MainScreen Composable Rendered") // Debugging log
        val context = LocalContext.current
        val surfaceView = remember { SurfaceView(context) }

        LaunchedEffect(Unit) {
            viewModel.openCamera(context = context)
        }

        val isRecording by viewModel.isRecording

        // Observe changes in isRecording and show a toast
        LaunchedEffect(isRecording) {
            Toast.makeText(
                context,
                if (isRecording) "Recording started" else "Recording stopped",
                Toast.LENGTH_SHORT
            ).show()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomCameraPreview(surfaceView)
                SetupButton(viewModel, surfaceView)
            }
        }
    }

    @Composable
    fun CustomCameraPreview(surfaceView: SurfaceView) {
        Box(
            contentAlignment = Alignment.TopCenter, // Centers the child
            modifier = Modifier.fillMaxWidth() // Makes Box take full available space
        ) {
            AndroidView(
                factory = { surfaceView },
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .padding(20.dp)
            )
        }
    }

    @Composable
    private fun SetupButton(viewModel: MainViewModel, surfaceView: SurfaceView) {
        Button(
            onClick = {
                if (hasPermissions()) {
                    viewModel.toggleButton(this@MainActivity, surfaceView)
                } else {
                    Toast.makeText(this@MainActivity, "Permissions Denied", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .padding(20.dp)
                .height(80.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Red,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Text(
                text = if (viewModel.isRecording.value)
                    stringResource(R.string.emergency_button_text_stop)
                else
                    stringResource(R.string.emergency_button_text_start),
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    private fun hasPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this@MainActivity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.i(TAG, "Permission granted")
                setContent {
                    MyApp()
                }
            } else {
                Log.i(TAG, "Permission denied")
            }
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        MyApp()
    }
}




