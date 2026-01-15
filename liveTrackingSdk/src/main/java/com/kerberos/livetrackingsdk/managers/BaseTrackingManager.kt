package com.kerberos.livetrackingsdk.managers

import android.content.Context
import com.adham.commonsdk.basemanager.BaseManager
import com.kerberos.livetrackingsdk.interfaces.ITrackingActionsListener
import com.kerberos.livetrackingsdk.interfaces.ITrackingSdkModeStatusListener

abstract class BaseTrackingManager(
    var context: Context,
    val trackingSdkModeStatusListener: ITrackingSdkModeStatusListener
) : BaseManager(context), ITrackingActionsListener {
//    abstract val locationManager: LocationTrackingManager?
//        get

    abstract fun initializeTrackingManager(): Boolean

    abstract fun destroyTrackingManager(): Boolean
}