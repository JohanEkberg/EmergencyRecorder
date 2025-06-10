package se.johan.emergencyrecorder.locator

data class LocatorUseCases(
    val getLocation : GetLocation,
    val getContinuousLocation : GetContinuousLocation
)

class LocatorException(
    message: String = "",
    val errorCode: Int = 0
) : Exception(message)
