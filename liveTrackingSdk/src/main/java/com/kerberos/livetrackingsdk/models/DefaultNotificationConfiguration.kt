package com.kerberos.livetrackingsdk.models

import android.app.Activity
import androidx.annotation.DrawableRes

data class DefaultNotificationConfiguration(
    val defaultIntentActivity: Class<out Activity>?, // Use 'out Activity' for covariance
    val contentTitle: String,
    val contentText: String,
    @DrawableRes val smallIcon: Int, // Annotation is part of the property
    val ticker: String,
    val notificationChannelId: String = "ENDLESS_SERVICE_CHANNEL_ID", // Default value
    val notificationChannelName: String = "Endless Service Channel", // Default value
    val notificationChannelDescription: String = "Notifications for the Endless Service" // Default value
)