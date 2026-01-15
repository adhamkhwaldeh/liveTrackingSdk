package com.kerberos.trackingSdk.models

data class TripTrackModel(
    val id: Int,

    val tripId: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Double,
    val altitude: Double,
    val accuracy: Double,
    val bearing: Double

)
