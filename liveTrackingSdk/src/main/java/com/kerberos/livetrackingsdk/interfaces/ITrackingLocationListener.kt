package com.kerberos.livetrackingsdk.interfaces

import android.location.Location

interface ITrackingLocationListener {
    fun onLocationUpdated(currentLocation: Location?)

    fun onLocationUpdateFailed(exception: Exception)
}