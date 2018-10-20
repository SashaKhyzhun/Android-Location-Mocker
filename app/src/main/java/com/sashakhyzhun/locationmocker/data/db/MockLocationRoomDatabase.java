package com.sashakhyzhun.locationmocker.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sashakhyzhun.locationmocker.data.model.MockLocation;

@Database(entities = {MockLocation.class}, version = 1)
public abstract class MockLocationRoomDatabase extends RoomDatabase {

    /**
     * @return Dao
     */
    public abstract MockLocationDao mockLocationDao();

    private static volatile MockLocationRoomDatabase INSTANCE;

    public static MockLocationRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MockLocationRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MockLocationRoomDatabase.class, "mock_location_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}