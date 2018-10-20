package com.sashakhyzhun.locationmocker.data.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask

import com.sashakhyzhun.locationmocker.data.model.MockLocation

@Database(entities = [MockLocation::class], version = 1)
public abstract class MockLocationRoomDatabase : RoomDatabase(), MockLocationDao {

    abstract fun mockLocationDao(): MockLocationDao


    companion object {
        @Volatile
        private var INSTANCE: MockLocationRoomDatabase? = null

        fun getDatabase(context: Context): MockLocationRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(MockLocationRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                MockLocationRoomDatabase::class.java, "mock_location_database")
                                .addCallback(sRoomDatabaseCallback)
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        private val sRoomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                PopulateDbAsync(INSTANCE!!).execute()
            }
        }
    }

    private class PopulateDbAsync(db: MockLocationRoomDatabase) : AsyncTask<Void, Void, Void>() {

        private val mDao: MockLocationDao = db.mockLocationDao()

        override fun doInBackground(vararg voids: Void): Void? {

            return null
        }
    }
}
