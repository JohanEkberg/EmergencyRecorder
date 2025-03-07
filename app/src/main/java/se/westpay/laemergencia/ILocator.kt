package se.westpay.laemergencia

import android.content.Context
import android.location.Location

interface ILocator {
    fun getLocation(context: Context, locationDataCallback: (Location?) -> Unit) : Boolean
    fun getContinuousLocation(context: Context, locationDataCallback: (Location?) -> Unit) : Boolean
}