package com.kerberos.livetrackingsdk.interfaces

import com.github.adhamkhwaldeh.commonsdk.listeners.callbacks.CallbackListener

interface ITrackingActionsListener : CallbackListener {
    fun onStartTracking(): Boolean

    fun onResumeTracking(): Boolean

    fun onPauseTracking(): Boolean

    fun onStopTracking(): Boolean
}