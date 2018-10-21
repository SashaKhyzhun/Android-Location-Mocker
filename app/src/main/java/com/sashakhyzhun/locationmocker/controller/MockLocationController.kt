package com.sashakhyzhun.locationmocker.controller

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import timber.log.Timber

class MockLocationController(private val providerName: String, private val ctx: Context) {


    private val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

    init {
        try {
            lm?.addTestProvider(providerName,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    0,
                    5)
            lm?.setTestProviderEnabled(providerName, true)
        } catch (e: SecurityException) {
            throw SecurityException("Not allowed to perform MOCK_LOCATION")
        }

    }

    /**
     * Pushes the location in the system (mock). This is where the magic gets done.
     *
     * @param lat latitude
     * @param lon longitude
     */
    fun pushLocation(lat: Double, lon: Double) {
        Timber.d("called")
        //val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val mockLocation = Location(providerName)
        mockLocation.latitude = lat
        mockLocation.longitude = lon
        mockLocation.altitude = 3.0
        mockLocation.time = System.currentTimeMillis()
        //mockLocation.setAccuracy(16F);
        mockLocation.speed = 0.01f
        mockLocation.bearing = 1f
        mockLocation.accuracy = 3f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mockLocation.bearingAccuracyDegrees = 0.1f
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mockLocation.verticalAccuracyMeters = 0.1f
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mockLocation.speedAccuracyMetersPerSecond = 0.01f
        }

        mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        lm?.setTestProviderLocation(providerName, mockLocation)
    }


    /**
     * Removes the provider
     */
    fun shutdown() {
        Timber.d("called")
        //val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            lm?.removeTestProvider(providerName)
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }


}
