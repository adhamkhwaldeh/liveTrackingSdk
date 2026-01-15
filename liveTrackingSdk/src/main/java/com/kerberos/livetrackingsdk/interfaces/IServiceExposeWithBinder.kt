package com.kerberos.livetrackingsdk.interfaces

import com.kerberos.livetrackingsdk.managers.LocationTrackingManager

interface IServiceExposeWithBinder {
    fun getLocationTrackingManager(): LocationTrackingManager
}