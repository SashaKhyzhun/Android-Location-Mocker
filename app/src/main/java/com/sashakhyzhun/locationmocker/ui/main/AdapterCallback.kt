package com.sashakhyzhun.locationmocker.ui.main

import com.sashakhyzhun.locationmocker.data.model.MockLocation

interface AdapterCallback {

    fun onLongClicked(mockLocation: MockLocation)

    fun onStartClicked(mockLocation: MockLocation)

    fun onStopClicked(mockLocation: MockLocation)

}
