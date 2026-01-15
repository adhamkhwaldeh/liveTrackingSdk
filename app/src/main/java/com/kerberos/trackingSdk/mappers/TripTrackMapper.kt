package com.kerberos.trackingSdk.mappers

import com.kerberos.trackingSdk.models.TripTrackModel
import com.kerberos.trackingSdk.orm.TripTrack

fun TripTrack.toTripTrackModel(): TripTrackModel {
    return TripTrackModel(
        id = this.id,
        tripId = this.tripId,
        longitude = this.longitude,
        latitude = this.latitude,
        timestamp = this.timestamp,
        speed = this.speed,

        altitude = this.altitude,
        accuracy = this.accuracy,
        bearing = this.bearing

    )
}

fun TripTrackModel.toTripTrack(): TripTrack {
    return TripTrack(
        id = this.id,
        tripId = this.tripId,
        longitude = this.longitude,
        latitude = this.latitude,
        timestamp = this.timestamp,
        speed = this.speed,

        altitude = this.altitude,
        accuracy = this.accuracy,
        bearing = this.bearing
    )
}
