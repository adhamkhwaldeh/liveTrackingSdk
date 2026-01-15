package com.kerberos.livetrackingsdk.interfaces

interface ITrackingActionsListener {
    fun onStartTracking(): Boolean

    fun onResumeTracking(): Boolean

    fun onPauseTracking(): Boolean

    fun onStopTracking(): Boolean
}