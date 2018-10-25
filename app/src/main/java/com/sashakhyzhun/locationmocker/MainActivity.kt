package com.sashakhyzhun.locationmocker

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var turnOn: Switch? = null
    private var isOn: Boolean = false
    private val myToast: Toast? = null
    private var provider: String? = null
    private var lm: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        turnOn = findViewById(R.id.mySwitch)


        //set it to off
        turnOn!!.isChecked = false
        //set a listener
        turnOn!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isOn = true
                displayToast("you are current in Los Angeles")
                setLocation()
            } else {
                isOn = false
                displayToast("mock location is off")
                if (provider != null)
                    lm!!.clearTestProviderLocation(provider)
            }
        }
    }


    private fun setLocation(): Boolean {
        lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val losAngeles = doubleArrayOf(34.046217, -118.288047)
        val location = Location(LocationManager.GPS_PROVIDER)
        provider = LocationManager.GPS_PROVIDER

        if (allowMockLocation())
            lm!!.addTestProvider(provider,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    0,
                    android.location.Criteria.ACCURACY_FINE)
        else {
            Log.d("addTestProvider", "ALLOW_MOCK_LOCATION is not specified in manifest.")
            return false
        }

        location.latitude = losAngeles[0]
        location.longitude = losAngeles[1]
        location.accuracy = 3f
        location.altitude = 0.0
        location.bearing = 0f
        location.speed = 0f
        location.time = System.currentTimeMillis()
        location.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

        lm!!.setTestProviderEnabled(provider, true)
        lm!!.setTestProviderStatus(
                provider,
                LocationProvider.AVAILABLE, null,
                System.currentTimeMillis()
        )

        lm!!.setTestProviderLocation(
                provider,
                location
        )

        return true
    }

    private fun allowMockLocation(): Boolean {
        val t = Settings.Secure.getString(this.contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION)
        return t != "0"
    }

    private fun displayToast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

}