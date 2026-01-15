package com.kerberos.trackingSdk.models

data class TripModel(
    val id: Int = 0,

    val startTime: Long,

    val endTime: Long? = null,

    val tripDuration: Long = 0,

    val totalDistance: Double = 0.0,

    var totalPoints: Long = 0,

    val isActive: Boolean
)
