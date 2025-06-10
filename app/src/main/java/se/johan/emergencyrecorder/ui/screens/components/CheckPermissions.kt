package se.johan.emergencyrecorder.ui.screens.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import se.johan.emergencyrecorder.viewmodels.HomeViewModel

@Composable
fun CheckPermissions(viewModel: HomeViewModel) {
    val context = LocalContext.current

    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // State to track all permissions granted
    var allPermissionsGranted by remember {
        mutableStateOf(
            permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        allPermissionsGranted = resultMap.all { it.value }
    }

    LaunchedEffect(Unit) {
        if (!allPermissionsGranted) {
            launcher.launch(permissions)
        }
    }

    // Notify ViewModel only when `hasPermission` changes
    LaunchedEffect(allPermissionsGranted) {
        viewModel.updatePermissionStatus(allPermissionsGranted)
    }
}
