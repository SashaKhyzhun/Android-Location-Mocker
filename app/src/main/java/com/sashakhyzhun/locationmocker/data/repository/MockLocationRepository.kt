package com.sashakhyzhun.locationmocker.data.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.sashakhyzhun.locationmocker.data.db.MockLocationDao
import com.sashakhyzhun.locationmocker.data.db.MockLocationRoomDatabase
import com.sashakhyzhun.locationmocker.data.model.MockLocation

class MockLocationRepository(app: Application) {

    private var dao: MockLocationDao
    private var allMockLocations: LiveData<List<MockLocation>>

    init {
        val db = MockLocationRoomDatabase.getDatabase(app)
        dao = db?.mockLocationDao()!!
        allMockLocations = dao.getAllLocations()
    }

    fun getAllMockLocations(): LiveData<List<MockLocation>> {
        return allMockLocations
    }

    fun insert(mockLocation: MockLocation) {
        InsertAsyncTask(dao).execute(mockLocation)
    }

    fun delete(mockLocation: MockLocation) {
        DeleteAsyncTask(dao).execute(mockLocation)
    }

    private class InsertAsyncTask(val dao: MockLocationDao): AsyncTask<MockLocation, Void, Void>() {
        override fun doInBackground(vararg mockLocation: MockLocation?): Void? {
            dao.insert(mockLocation[0]!!)
            return null
        }
    }

    private class DeleteAsyncTask(val dao: MockLocationDao): AsyncTask<MockLocation, Void, Void>() {
        override fun doInBackground(vararg mockLocation: MockLocation?): Void? {
            dao.delete(mockLocation[0]?.title!!)
            return null
        }
    }
}