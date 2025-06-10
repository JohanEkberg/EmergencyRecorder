package se.johan.emergencyrecorder.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import se.johan.emergencyrecorder.ui.screens.AppScreens
import se.johan.emergencyrecorder.ui.screens.HomeScreen

@Composable
fun EmergencyRecorderNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        modifier = Modifier.padding(innerPadding),
        startDestination = AppScreens.HomeScreenIdentifier
    ) {
        // Map splash screen identifier with the splash screen
        composable<AppScreens.HomeScreenIdentifier> {
            HomeScreen(navController)
        }
    }
}