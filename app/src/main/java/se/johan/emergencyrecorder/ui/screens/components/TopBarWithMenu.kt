package se.johan.emergencyrecorder.ui.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithMenu() {
    var expanded by remember { mutableStateOf(false) }  // State to control menu visibility

//    // Accessing the window insets to get the status bar height
//    val insets = LocalView.current.rootWindowInsets
//    // Correct way to get the status bar height
//    val statusBarHeightPx = insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top
//        ?: 0
//
//    // Convert from pixels to dp
//    val statusBarHeight = with(LocalDensity.current) {
//        statusBarHeightPx.toDp()
//    }

    TopAppBar(
        title = { Text(text = "Emergency Recorder", color = Color.White) },
        //modifier = Modifier.Companion.padding(top = statusBarHeight),
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Test") },
                    onClick = {
//                        showMenu = false
//                        navController.navigate(AppScreens.SettingsScreenIdentifier)
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTopBarWithMenu() {
    TopBarWithMenu()
}