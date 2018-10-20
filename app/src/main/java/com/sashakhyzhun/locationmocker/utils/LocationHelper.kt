package com.sashakhyzhun.locationmocker.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import timber.log.Timber
import java.lang.Exception

object LocationHelper {

    fun Context.checkMyLocation(): Pair<Double, Double> {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var location = Location(LocationManager.GPS_PROVIDER)
        try {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } catch (e: SecurityException) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        val latitude = location.latitude
        val longitude = location.longitude

        return Pair(latitude, longitude)
    }

}