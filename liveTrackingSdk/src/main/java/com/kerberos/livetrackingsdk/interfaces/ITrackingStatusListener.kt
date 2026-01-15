package com.kerberos.livetrackingsdk.interfaces

import com.kerberos.livetrackingsdk.enums.TrackingState

interface ITrackingStatusListener {
    fun onTrackingStateChanged(trackingState: TrackingState)
}