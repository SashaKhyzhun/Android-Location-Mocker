package com.sashakhyzhun.locationmocker.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.LocationManager
import com.sashakhyzhun.locationmocker.data.model.MockLocation
import com.sashakhyzhun.locationmocker.data.repository.MockLocationRepository
import com.sashakhyzhun.locationmocker.controller.MockLocationController
import timber.log.Timber

class ViewModel constructor(app: Application) : AndroidViewModel(app) {

//    companion object {
//        const val KEEP_GOING = 0
//        const val SCHEDULE_REQUEST_CODE = 1
//        const val sharedPrefKey = "cl.coders.mockposition.sharedpreferences"
//    }

    private var mRepository: MockLocationRepository = MockLocationRepository(app)
    private var mAllLocations: LiveData<List<MockLocation>>

//    private var serviceIntent: Intent? = null
//    private var pendingIntent: PendingIntent? = null
//    private var alarmManager: AlarmManager? = null

    var mockNetwork: MockLocationController? = null
    var mockGps: MockLocationController? = null

//    var timeInterval: Int = 0
//    private var howManyTimes: Int = 0
//    private var endTime: Long = 0
//    private var currentVersion: Int = 0
//
//    var sharedPref: SharedPreferences? = null
//    var editor: SharedPreferences.Editor? = null

    init {
        mAllLocations = mRepository.getAllMockLocations()

//        alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
//        sharedPref = app.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
//        editor = sharedPref?.edit()
//
//        try {
//            val pInfo = app.packageManager.getPackageInfo(app.packageName, 0)
//            currentVersion = pInfo.versionCode
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
//
//        //checkSharedPrefs()
//
//        howManyTimes = Integer.parseInt(sharedPref?.getString("howManyTimes", "1"))
//        timeInterval = Integer.parseInt(sharedPref?.getString("timeInterval", "10"))
//
//        endTime = sharedPref?.getLong("endTime", 0)!!
//
//        if (pendingIntent != null && endTime > System.currentTimeMillis()) {
//            //changeButtonToStop();
//        } else {
//            endTime = 0
//            editor?.putLong("endTime", 0)
//            editor?.commit()
//        }


    }

//    private fun checkSharedPrefs() {
//        val version = sharedPref!!.getInt("version", 0)
//        val lat = sharedPref!!.getString("lat", "N/A")
//        val lng = sharedPref!!.getString("lng", "N/A")
//        val howManyTimes = sharedPref!!.getString("howManyTimes", "N/A")
//        val timeInterval = sharedPref!!.getString("timeInterval", "N/A")
//        val endTime = sharedPref!!.getLong("endTime", 0)
//
//        if (version != currentVersion) {
//            editor!!.putInt("version", currentVersion)
//            editor!!.commit()
//        }
//
//        try {
//            java.lang.Double.parseDouble(lat)
//            java.lang.Double.parseDouble(lng)
//            java.lang.Double.parseDouble(howManyTimes)
//            java.lang.Double.parseDouble(timeInterval)
//        } catch (e: NumberFormatException) {
//            editor!!.clear()
//            editor!!.putString("lat", lat)
//            editor!!.putString("lng", lng)
//            editor!!.putInt("version", currentVersion)
//            editor!!.putString("howManyTimes", "1")
//            editor!!.putString("timeInterval", "10")
//            editor!!.putLong("endTime", 0)
//            editor!!.commit()
//            e.printStackTrace()
//        }
//
//    }

    fun getAllLocations(): LiveData<List<MockLocation>> = mAllLocations

    fun insert(mockLocation: MockLocation) {
        mRepository.insert(mockLocation)
    }

    fun delete(mockLocation: MockLocation) {
        mRepository.delete(mockLocation)
    }


    /**
     * Apply a mocked location, and start an alarm to keep doing it if howManyTimes is > 1
     * This method is called when "Apply" button is pressed.
     */
    fun applyLocation(ctx: Context, mockLocation: MockLocation) {
        Timber.d("called")
        val title = mockLocation.title
        val lat = mockLocation.latitude
        val lng = mockLocation.longitude

        try {
            mockNetwork = MockLocationController(LocationManager.NETWORK_PROVIDER, ctx)
            mockGps = MockLocationController(LocationManager.GPS_PROVIDER, ctx)
        } catch (e: SecurityException) {
            e.printStackTrace()
            stopMockingLocation()
            return
        }

        exec(lat, lng)

//        if (!hasEnded()) {
//            Toast.makeText(ctx, ctx.resources.getString(R.string.MainActivity_MockLocRunning), Toast.LENGTH_SHORT).show()
//            setAlarm(10, ctx)
//        } else {
//            stopMockingLocation()
//        }
    }

    /**
     * Set a mocked location.
     *
     * @param lat latitude
     * @param lng longitude
     */
    private fun exec(lat: Double, lng: Double) {
        Timber.d("called")
        try {
            mockNetwork?.pushLocation(lat, lng)
            mockGps?.pushLocation(lat, lng)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    fun stopMockingLocation() {
        Timber.d("called")
        if (mockNetwork != null) mockNetwork?.shutdown()
        if (mockGps != null) mockGps?.shutdown()
    }

//    /**
//     * Check if mocking location should be stopped
//     * @return true if it has ended
//     */
//    fun hasEnded(): Boolean {
//        return when {
//            howManyTimes == KEEP_GOING -> false
//            System.currentTimeMillis() > endTime -> true
//            else -> false
//        }
//    }

//    /**
//     * Sets the next alarm accordingly to <seconds>
//     *
//     * @param seconds number of seconds
//     */
//    fun setAlarm(seconds: Int, ctx: Context) {
//        serviceIntent = Intent(ctx, ApplyMockBroadcastReceiver::class.java)
//        pendingIntent = PendingIntent.getBroadcast(ctx, SCHEDULE_REQUEST_CODE, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + seconds * 1000, pendingIntent)
//            } else {
//                alarmManager?.setExact(AlarmManager.RTC, System.currentTimeMillis() + timeInterval * 1000, pendingIntent)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

}
