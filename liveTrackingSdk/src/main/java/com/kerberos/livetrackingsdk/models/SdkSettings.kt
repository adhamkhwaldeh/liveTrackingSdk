package com.kerberos.livetrackingsdk.models

data class SdkSettings(
    val locationUpdateInterval: Long,
    val minDistanceMeters: Float?,
    val backgroundTrackingToggle: Boolean
)