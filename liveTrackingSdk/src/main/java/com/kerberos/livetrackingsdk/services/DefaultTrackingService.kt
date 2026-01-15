package com.kerberos.livetrackingsdk.services

import android.location.Location
import com.kerberos.livetrackingsdk.models.DefaultNotificationConfiguration
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class DefaultTrackingService : BaseTrackingService() {

    override val serviceClassForRestart
        get(): Class<out DefaultTrackingService> {
            return DefaultTrackingService::class.java
        }

    override fun onLocationUpdated(currentLocation: Location?) {

    }

    override fun onLocationUpdateFailed(exception: Exception) {

    }

    override val defaultNotificationConfiguration: DefaultNotificationConfiguration?
        get() = DefaultNotificationConfiguration(
            notificationChannelId = "trip_channel_id",
            notificationChannelName = "Trip Tracking",
            notificationChannelDescription = "Notifications for trip tracking",
            contentTitle = "Trip in Progress",
            contentText = "Your trip is being tracked",
            smallIcon = android.R.mipmap.sym_def_app_icon,
            defaultIntentActivity = null,
            ticker = "Trip Tracking Active"
        )
}