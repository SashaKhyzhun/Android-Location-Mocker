package com.sashakhyzhun.locationmocker

import android.app.Application
import com.sashakhyzhun.locationmocker.utils.ThreadAwareTree
import timber.log.Timber

class LocationMocker : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(ThreadAwareTree)
        }


    }

}