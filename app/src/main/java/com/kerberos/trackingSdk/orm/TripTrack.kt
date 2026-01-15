package com.kerberos.trackingSdk.orm

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.kerberos.trackingSdk.mappers.toTripTrackModel

@Keep
@Entity
class TripTrack() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "tripId")
    var tripId: Int = 0

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0

    @ColumnInfo(name = "speed")
    var speed: Double = 0.0

    @ColumnInfo(name = "altitude")
    var altitude: Double = 0.0

    @ColumnInfo(name = "accuracy")
    var accuracy: Double = 0.0

    @ColumnInfo(name = "bearing")
    var bearing: Double = 0.0

    @Ignore
    constructor(
        id: Int = 0,
        tripId: Int,
        longitude: Double,
        latitude: Double,
        timestamp: Long,
        speed: Double,

        altitude: Double,
        accuracy: Double,
        bearing: Double

    ) : this() {
        this.id = id
        this.tripId = tripId
        this.longitude = longitude
        this.latitude = latitude
        this.timestamp = timestamp
        this.speed = speed

        this.altitude = altitude
        this.accuracy = accuracy
        this.bearing = bearing
    }
}
