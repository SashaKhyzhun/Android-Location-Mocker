package com.sashakhyzhun.locationmocker.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.location.Location
import android.support.annotation.NonNull

/**
 * A data class used to save data of a mock target
 *
 * Public Attribute
 * - [longitude]
 * - [latitude]
 * - [enabled]
 * - [title]
 * - [interval]
 * - [accuracy]
 * - [altitude]
 *
 * Getters and Setters
 * - [location]
 *
 * @param [latitude] Latitude of the mock target
 * @param [longitude] Longitude of the mock target
 * @param [enabled] If enabled when Service running
 *
 * @property [location] Getter and Setter of location ([latitude] and [longitude])
 */

@Entity(tableName = "mock_location_table")
data class MockLocation (
        @PrimaryKey @NonNull @ColumnInfo(name = "title") var title: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var enabled: Boolean = true,
        var interval: Long = 60_000, // 60 sec
        var accuracy: Float = 5F,
        var altitude: Double? = null
) {


    var location: Location
        set(value) {
            longitude = value.longitude
            latitude = value.latitude
            accuracy = if (value.hasAccuracy()) value.accuracy else 5F
            altitude = if (value.hasAltitude()) value.altitude else null
        }
        get() {
            val location = Location("")
            location.longitude = longitude
            location.latitude = latitude
            location.accuracy = accuracy
            if (altitude != null) location.altitude = altitude!!
            return location
        }

}