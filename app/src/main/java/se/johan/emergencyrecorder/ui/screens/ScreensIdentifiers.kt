package se.johan.emergencyrecorder.ui.screens

import kotlinx.serialization.Serializable

/***
 * Below classes is to have type safe in navigation.
 */
@Serializable
sealed class AppScreens(val route: String) {

    @Serializable
    data object HomeScreenIdentifier
        : AppScreens("se.johan.emergencyrecorder.ui.screens.AppScreens.HomeScreenIdentifier")
}
