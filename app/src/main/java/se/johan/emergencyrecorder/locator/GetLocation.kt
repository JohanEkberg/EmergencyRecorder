package se.johan.emergencyrecorder.locator

import android.content.Context
import android.location.Location

class GetLocation(private val repository: LocatorRepository) {
    @Throws(LocatorException::class)
    operator fun invoke(context: Context, locationDataCallback: (Location?) -> Unit) : Boolean {
        return try {
            repository.getLocation(context, locationDataCallback)
        } catch(e: Exception) {
            throw LocatorException("Failed to get location, exception: ${e.message}")
        }
    }
}