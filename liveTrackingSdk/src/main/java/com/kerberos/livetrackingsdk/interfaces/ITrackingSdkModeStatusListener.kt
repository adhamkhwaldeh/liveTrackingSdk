package com.kerberos.livetrackingsdk.interfaces

import com.kerberos.livetrackingsdk.enums.LiveTrackingMode
import com.kerberos.livetrackingsdk.managers.LocationTrackingManager

interface ITrackingSdkModeStatusListener {
    fun onTrackingSDKModeInitialized(
        locationTrackingManager: LocationTrackingManager,
        liveTrackingMode: LiveTrackingMode
    )
}