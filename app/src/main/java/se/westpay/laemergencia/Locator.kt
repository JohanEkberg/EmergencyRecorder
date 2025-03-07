package se.westpay.laemergencia

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class Locator(context: Context) : ILocator {
    private val _fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    override fun getLocation(context: Context, locationDataCallback: (Location?) -> Unit ) : Boolean {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "GPS permission not granted!")
            return false
        }

        _fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            locationDataCallback(location)
        }
        return true
    }

    override fun getContinuousLocation(
        context: Context,
        locationDataCallback: (Location?) -> Unit
    ): Boolean {
        return try {

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
                setMinUpdateIntervalMillis(5000)  // Minimum interval for updates
            }.build()

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "GPS permission not granted!")
                false
            } else {
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        for (location in locationResult.locations) {
                            Log.i("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                            locationDataCallback(location)
                        }
                    }
                }

                _fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get continuous location, exception: ${e.message}")
            false
        }
    }
}