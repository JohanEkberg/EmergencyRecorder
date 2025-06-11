package se.johan.emergencyrecorder.ui.screens

import android.content.Context
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import se.johan.emergencyrecorder.R
import se.johan.emergencyrecorder.ui.screens.components.CheckPermissions
import se.johan.emergencyrecorder.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe permission state
    val isPermissionGranted = viewModel.isPermissionGranted.collectAsState().value

    CheckPermissions(viewModel = viewModel)

    val context = LocalContext.current
    val surfaceView = remember { SurfaceView(context) }

    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted) {
            viewModel.openCamera(context = context)
        }
    }

    val isCameraOpen = viewModel.isCameraOpen.collectAsState()

    // Observe changes in isCameraOpen
    LaunchedEffect(isCameraOpen.value) {
        viewModel.deleteFilesOnHost(context = context)
        if (isCameraOpen.value) {
            viewModel.getCurrentLocation(context = context)
            viewModel.startCamera(context = context, surfaceView = surfaceView)
        }
    }

    val isRecording = viewModel.isRecording.collectAsState()

    // Observe changes in isRecording and show a toast
    LaunchedEffect(isRecording.value) {
        snackbarHostState.showSnackbar(if (isRecording.value) "Recording started" else "Recording stopped")
    }

    Scaffold (
        //snackbarHost = @Composable { SnackbarHost(snackbarHostState) }
        snackbarHost = {
            // Adjust the position of the SnackbarHost here
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(contentPadding).background(Color.Black)) {
            CustomCameraPreview(surfaceView, modifier = Modifier.fillMaxSize())
            SetupButton(
                context = context,
                viewModel = viewModel,
                surfaceView = surfaceView,
                isRecording = isRecording.value,
                modifier = Modifier
                    .align(Alignment.BottomCenter) // This is the crucial line for alignment
                    .padding(horizontal = 16.dp, vertical = 32.dp) // Add padding

            )
        }
    }
}

@Composable
fun CustomCameraPreview(surfaceView: SurfaceView, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { surfaceView },
        modifier = modifier
    )
}

@Composable
private fun SetupButton(
    context: Context,
    viewModel: HomeViewModel,
    surfaceView: SurfaceView,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            viewModel.toggleButton(context, surfaceView)
        },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = if (isRecording)
                stringResource(R.string.emergency_button_text_stop)
            else
                stringResource(R.string.emergency_button_text_start),
            style = MaterialTheme.typography.labelLarge
        )
    }
}