package com.kerberos.trackingSdk.mappers

import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.orm.Trip

fun Trip.toTripModel(): TripModel {
    return TripModel(
        id = this.id,
        startTime = this.startTime,
        endTime = this.endTime,
        tripDuration = this.tripDuration,
        totalDistance = this.totalDistance,
        totalPoints = this.totalPoints,
        isActive = this.isActive
    )
}

fun TripModel.toTrip(): Trip {
    return Trip(
        id = this.id,
        startTime = this.startTime,
        endTime = this.endTime,
        tripDuration = this.tripDuration,
        totalDistance = this.totalDistance,
        totalPoints = this.totalPoints,
        isActive = this.isActive
    )
}
