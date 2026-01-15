package com.kerberos.trackingSdk.orm

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Keep
@Entity
class Trip() {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "startTime")
    var startTime: Long = 0

    @ColumnInfo(name = "endTime")
    var endTime: Long? = null

    @ColumnInfo(name = "tripDuration")
    var tripDuration: Long = 0

    @ColumnInfo(name = "totalDistance")
    var totalDistance: Double = 0.0

    @ColumnInfo(name = "isActive")
    var isActive: Boolean = false

    @ColumnInfo(name = "totalPoints")
    var totalPoints: Long = 0

    @Ignore
//    @JvmOverloads
    constructor(
        id: Int = 0,
        startTime: Long = 0,
        endTime: Long? = null,
        tripDuration: Long = 0,
        totalDistance: Double = 0.0,
        totalPoints: Long = 0,
        isActive: Boolean = false
    ) : this() {
        this.id = id
        this.startTime = startTime
        this.endTime = endTime
        this.tripDuration = tripDuration
        this.totalDistance = totalDistance
        this.totalPoints = totalPoints
        this.isActive = isActive
    }
}
