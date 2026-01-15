package com.kerberos.trackingSdk

import android.location.Location
import android.util.Log
import com.kerberos.livetrackingsdk.services.BaseTrackingService
import com.kerberos.livetrackingsdk.models.DefaultNotificationConfiguration
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class TripBackgroundService : BaseTrackingService() {

    override val serviceClassForRestart
        get(): Class<out TripBackgroundService> {
            return TripBackgroundService::class.java
        }

    override fun onLocationUpdated(currentLocation: Location?) {
        Log.e("Location", "onLocationUpdated")
    }

    override fun onLocationUpdateFailed(exception: Exception) {
        Log.e(
            "Location update failed: ${exception.message}", exception.message ?: ""
        )
    }

    override val defaultNotificationConfiguration: DefaultNotificationConfiguration?
        get() = DefaultNotificationConfiguration(
            notificationChannelId = "trip_channel_id",
            notificationChannelName = "Trip Tracking",
            notificationChannelDescription = "Notifications for trip tracking",
            contentTitle = "Trip in Progress",
            contentText = "Your trip is being tracked",
            smallIcon = R.mipmap.ic_launcher,
            defaultIntentActivity = MainActivity::class.java,
            ticker = "Trip Tracking Active"
        )

}