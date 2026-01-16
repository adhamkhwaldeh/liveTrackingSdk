package com.kerberos.livetrackingsdk.interfaces

import com.github.adhamkhwaldeh.commonsdk.listeners.callbacks.CallbackListener
import com.kerberos.livetrackingsdk.enums.LiveTrackingMode
import com.kerberos.livetrackingsdk.managers.LocationTrackingManager

interface ITrackingSdkModeStatusListener : CallbackListener {
    fun onTrackingSDKModeInitialized(
        locationTrackingManager: LocationTrackingManager,
        liveTrackingMode: LiveTrackingMode
    )
}