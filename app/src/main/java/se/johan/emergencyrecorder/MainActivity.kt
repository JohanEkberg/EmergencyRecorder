package se.johan.emergencyrecorder

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import se.johan.emergencyrecorder.ui.navigation.EmergencyRecorderNavGraph
import se.johan.emergencyrecorder.ui.screens.components.TopBarWithMenu
import se.johan.emergencyrecorder.ui.theme.EmergencyRecorderTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent {
                EmergencyRecorderTheme {
                    val navController = rememberNavController()
                    Scaffold(
                        topBar = { TopBarWithMenu() }
                    ) { innerPadding ->
                        EmergencyRecorderNavGraph(navController, innerPadding)
                    }
                }
            }
        }
    }

//    @Composable
//    fun MyApp() {
//        Scaffold(
//            topBar = { TopBarWithMenu() }
//        ) { paddingValues ->
//            // Avoid re-instantiate the view model, make Android to remember the viewModel
//            val viewModel = viewModel<MainViewModel>()
//            MainScreen(viewModel, modifier = Modifier.padding(paddingValues))
//        }
//    }



//    private fun hasPermissions(): Boolean {
//        return REQUIRED_PERMISSIONS.all {
//            ContextCompat.checkSelfPermission(this@MainActivity, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                Log.i(TAG, "Permission granted")
//                setContent {
//                    MyApp()
//                }
//            } else {
//                Log.i(TAG, "Permission denied")
//            }
//        }
//    }

//    companion object {
//        private val REQUIRED_PERMISSIONS = arrayOf(
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//        private const val REQUEST_CODE_PERMISSIONS = 1001
//    }
//
//    @Preview(showBackground = true)
//    @Composable
//    fun MainScreenPreview() {
//        MyApp()
//    }
//}




