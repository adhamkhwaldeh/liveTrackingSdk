package com.kerberos.livetrackingsdk.managers

import android.content.Context
import com.github.adhamkhwaldeh.commonsdk.listeners.errors.ErrorListener
import com.github.adhamkhwaldeh.commonsdk.logging.Logger
import com.github.adhamkhwaldeh.commonsdk.managers.BaseManagerImpl
import com.kerberos.livetrackingsdk.configs.TrackingConfig
import com.kerberos.livetrackingsdk.interfaces.ITrackingActionsListener
import com.kerberos.livetrackingsdk.interfaces.ITrackingSdkModeStatusListener

abstract class BaseTrackingManager(
    config: TrackingConfig,
    logger: Logger,
    var context: Context,
    val trackingSdkModeStatusListener: ITrackingSdkModeStatusListener,
) : BaseManagerImpl<ITrackingActionsListener, ErrorListener, TrackingConfig>(
    config,
    logger
), ITrackingActionsListener {

    abstract fun initializeTrackingManager(): Boolean

    abstract fun destroyTrackingManager(): Boolean
}