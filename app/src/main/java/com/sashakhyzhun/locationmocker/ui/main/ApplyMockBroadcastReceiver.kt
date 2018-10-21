package com.sashakhyzhun.locationmocker.ui.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences


public class ApplyMockBroadcastReceiver(
        private var alarmManager: AlarmManager,
        private var serviceIntent: Intent,
        private var pendingIntent: PendingIntent,
        private var sharedPref: SharedPreferences,
        private var editor: SharedPreferences.Editor,
        private var viewModel: ViewModel
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        try {
            val lat = java.lang.Double.parseDouble(sharedPref.getString("lat", "0"))
            val lng = java.lang.Double.parseDouble(sharedPref.getString("lng", "0"))

            if (!viewModel.hasEnded()) {
                viewModel.setAlarm(viewModel.timeInterval, context)
            } else {
                viewModel.stopMockingLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
