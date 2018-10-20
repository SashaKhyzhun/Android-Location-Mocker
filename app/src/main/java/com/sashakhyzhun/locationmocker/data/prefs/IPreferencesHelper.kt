package com.sashakhyzhun.locationmocker.data.prefs

import com.sashakhyzhun.locationmocker.data.model.MockLocation

interface IPreferencesHelper {

    fun storeMockLocation(item: MockLocation)

    fun retrieve(title: String): MockLocation

    fun retrieveAll(): List<MockLocation>

}