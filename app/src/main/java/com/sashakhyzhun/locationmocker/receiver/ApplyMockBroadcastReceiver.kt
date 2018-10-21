package com.sashakhyzhun.locationmocker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.sashakhyzhun.locationmocker.ui.main.ViewModel
import timber.log.Timber


class ApplyMockBroadcastReceiver : BroadcastReceiver() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var viewModel: ViewModel

    fun initiate(sharedPref: SharedPreferences, viewModel: ViewModel) {
        this.sharedPref = sharedPref
        this.viewModel = viewModel
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("called")
//        try {
//            val lat = java.lang.Double.parseDouble(sharedPref.getString("lat", "0"))
//            val lng = java.lang.Double.parseDouble(sharedPref.getString("lng", "0"))
//
//            if (!viewModel.hasEnded()) {
//                viewModel.setAlarm(viewModel.timeInterval, context)
//            } else {
//                viewModel.stopMockingLocation()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }


}
