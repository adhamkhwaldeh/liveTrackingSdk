package com.kerberos.livetrackingsdk.managers

import android.content.Context
import com.kerberos.livetrackingsdk.enums.LiveTrackingMode
import com.kerberos.livetrackingsdk.interfaces.ITrackingSdkModeStatusListener


class ForegroundTrackingManager(
    context: Context,
    trackingSdkModeStatusListener: ITrackingSdkModeStatusListener
) : BaseTrackingManager(context, trackingSdkModeStatusListener) {

    private val locationTrackingManager: LocationTrackingManager by lazy {
        LocationTrackingManager(context)
    }

    //#region SDK actions
    override fun initializeTrackingManager(): Boolean {
        trackingSdkModeStatusListener.onTrackingSDKModeInitialized(
            locationTrackingManager,
            LiveTrackingMode.FOREGROUND_SERVICE
        )
        return true
    }

    override fun destroyTrackingManager(): Boolean {
        return locationTrackingManager.onStopTracking()
    }
    //#endregion


    //#region tracking functions
    override fun onStartTracking(): Boolean {
        try {
            return locationTrackingManager.onStartTracking()
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onResumeTracking(): Boolean {
        try {
            return locationTrackingManager.onResumeTracking()
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onPauseTracking(): Boolean {
        try {
            return locationTrackingManager.onPauseTracking()
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onStopTracking(): Boolean {
        try {
            return locationTrackingManager.onStopTracking()
        } catch (ex: Exception) {

        }
        return false
    }
    //#endregion
}