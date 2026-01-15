package com.kerberos.livetrackingsdk.backgroundTracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.kerberos.livetrackingsdk.enums.TrackingState
import com.kerberos.livetrackingsdk.services.BaseTrackingService
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //TODO I need to check if the intent action is null or not running the service without any action will crash the app
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && !BackgroundTrackingHelper.isServiceRunning(
                context = context,
                serviceClass = BaseTrackingService::class.java
            )
        ) {
            Intent(context, BaseTrackingService::class.java).also {
                it.action = TrackingState.IDLE.name
                //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                log("Starting the service in >=26 Mode from a BroadcastReceiver")
                ContextCompat.startForegroundService(context, it)
//                    return
//                }
//                log("Starting the service in < 26 Mode from a BroadcastReceiver")
//                context.startService(it)
            }
        }
    }
}