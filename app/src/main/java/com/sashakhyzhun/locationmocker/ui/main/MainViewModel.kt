package com.sashakhyzhun.locationmocker.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.sashakhyzhun.locationmocker.data.model.MockLocation
import com.sashakhyzhun.locationmocker.data.repository.MockLocationRepository

class MainViewModel constructor(app: Application) : AndroidViewModel(app) {

    private val mRepository: MockLocationRepository = MockLocationRepository(app)
    private var mAllLocations: LiveData<List<MockLocation>>

    init {
        mAllLocations = mRepository.getAllMockLocations()
    }

    fun getAllLocations(): LiveData<List<MockLocation>> = mAllLocations

    fun insert(mockLocation: MockLocation) {
        mRepository.insert(mockLocation)
    }

}
