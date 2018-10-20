package com.sashakhyzhun.locationmocker.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sashakhyzhun.locationmocker.data.model.MockLocation

@Dao
interface MockLocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(mockLocation: MockLocation)

    @Query("DELETE FROM mock_location_table WHERE title = :title")
    fun delete(title: String)

    @Query("SELECT * FROM mock_location_table")
    fun getAllLocations(): LiveData<List<MockLocation>>

}