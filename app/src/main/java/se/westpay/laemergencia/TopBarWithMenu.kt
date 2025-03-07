package se.westpay.laemergencia

import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsCompat


@Composable
fun TopBarWithMenu() {
    var expanded by remember { mutableStateOf(false) }  // State to control menu visibility

    // Accessing the window insets to get the status bar height
    val insets = LocalView.current.rootWindowInsets
    // Correct way to get the status bar height
    val statusBarHeightPx = insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top
        ?: 0

    // Convert from pixels to dp
    val statusBarHeight = with(LocalDensity.current) {
        statusBarHeightPx.toDp()
    }

    TopAppBar(
        title = { Text("La Emergencia") },
        backgroundColor = Color.DarkGray,
        contentColor = Color.White,
        modifier = Modifier.padding(top = statusBarHeight),
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = { /* Handle Settings Click */ }) {
                    Text("Settings")
                }
                DropdownMenuItem(onClick = { /* Handle Logout Click */ }) {
                    Text("Logout")
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTopBarWithMenu() {
    TopBarWithMenu()
}