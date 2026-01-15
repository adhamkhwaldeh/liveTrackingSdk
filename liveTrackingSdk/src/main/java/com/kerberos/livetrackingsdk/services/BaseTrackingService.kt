package com.kerberos.livetrackingsdk.services

import android.R
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.RemoteException
import android.os.SystemClock
import androidx.annotation.RequiresApi
import com.kerberos.livetrackingsdk.ITrackingService
import com.kerberos.livetrackingsdk.enums.TrackingState
import com.kerberos.livetrackingsdk.exceptions.DefaultNotificationConfigurationNotImplementedException
import com.kerberos.livetrackingsdk.interfaces.IServiceExposeWithBinder
import com.kerberos.livetrackingsdk.interfaces.ITrackingActionsListener
import com.kerberos.livetrackingsdk.interfaces.ITrackingLocationListener
import com.kerberos.livetrackingsdk.managers.LocationTrackingManager
import com.kerberos.livetrackingsdk.models.DefaultNotificationConfiguration
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber

@DelicateCoroutinesApi
abstract class BaseTrackingService : Service(), ITrackingActionsListener,
    ITrackingLocationListener {

    //#region Customized properties and functions
    abstract val serviceClassForRestart: Class<out Service>
        get

    abstract val defaultNotificationConfiguration: DefaultNotificationConfiguration?

    @RequiresApi(Build.VERSION_CODES.O)
    open fun createNotification(): Notification {
        if (defaultNotificationConfiguration == null) {
            throw DefaultNotificationConfigurationNotImplementedException()
        }
        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        val channel = NotificationChannel(
            defaultNotificationConfiguration!!.notificationChannelId,
            defaultNotificationConfiguration!!.notificationChannelName,
            NotificationManager.IMPORTANCE_HIGH
        ).let {
            it.description = defaultNotificationConfiguration!!.notificationChannelDescription
            it.enableLights(true)
            it.lightColor = Color.RED
            it.enableVibration(true)
            it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            it
        }
        notificationManager.createNotificationChannel(channel)


        val builder: Notification.Builder =
            Notification.Builder(
                this,
                defaultNotificationConfiguration!!.notificationChannelId
            ).setContentTitle(defaultNotificationConfiguration!!.contentTitle)
                .setContentText(defaultNotificationConfiguration!!.contentText)
                .setSmallIcon(defaultNotificationConfiguration!!.smallIcon)
                .setTicker(defaultNotificationConfiguration!!.ticker)
                .setOngoing(true) // Makes the notification non-dismissable, typical for foreground services
                // Don't make sound/vibrate for subsequent updates
                .setOnlyAlertOnce(true)        // Add actions based on the current state


        when (locationTrackingManager.trackingState) {
            TrackingState.IDLE, TrackingState.STOPPED -> {
                builder.addAction(
                    createAction(
                        R.drawable.ic_media_play,
                        "Start",
                        ACTION_START_TRACKING,
                        REQUEST_CODE_START
                    )
                )
            }

            TrackingState.STARTED -> {
                builder.addAction(
                    createAction(
                        R.drawable.ic_media_pause,
                        "Pause",
                        ACTION_PAUSE_TRACKING,
                        REQUEST_CODE_PAUSE
                    )
                )
                // Optionally add a stop action
                builder.addAction(
                    createAction(
                        R.drawable.ic_notification_clear_all,
                        "Stop",
                        ACTION_STOP_TRACKING,
                        REQUEST_CODE_STOP
                    )
                )
            }

            TrackingState.PAUSED -> {
                builder.addAction(
                    createAction(
                        R.drawable.ic_media_pause,
                        "Resume",
                        ACTION_RESUME_TRACKING,
                        REQUEST_CODE_RESUME
                    )
                )
                builder.addAction(
                    createAction(
                        R.drawable.ic_notification_clear_all,
                        "Stop",
                        ACTION_STOP_TRACKING,
                        REQUEST_CODE_STOP
                    )
                )
            }


        }

        defaultNotificationConfiguration!!.defaultIntentActivity?.let { defaultIntentActivity ->
            val pendingIntent: PendingIntent =
                Intent(
                    this,
                    defaultIntentActivity::class.java
                ).let { notificationIntent ->

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.getActivity(
                            this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_MUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(
                            this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                        )
                    }
                }
            builder.setContentIntent(pendingIntent)
        }


        return builder.build()
    }

    //#endregion

    private val locationTrackingManager: LocationTrackingManager by lazy {
        LocationTrackingManager(applicationContext)
    }

    private var wakeLock: PowerManager.WakeLock? = null

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val itsBinder: ITrackingService.Stub =
        object : ITrackingService.Stub(), IServiceExposeWithBinder {
            @Throws(RemoteException::class)
            override fun startTracking(): Boolean {
                val result = this@BaseTrackingService.onStartTracking()
                if (result) {
                    onStartCommand(Intent(ACTION_START_TRACKING), 0, 0)
                }
                return result
            }

            @Throws(RemoteException::class)
            override fun pauseTracking(): Boolean {
                onStartCommand(Intent(ACTION_PAUSE_TRACKING), 0, 0)
                return this@BaseTrackingService.onPauseTracking()
            }

            @Throws(RemoteException::class)
            override fun resumeTracking(): Boolean {
                return this@BaseTrackingService.onResumeTracking()
            }

            @Throws(RemoteException::class)
            override fun stopTracking(): Boolean {
                return this@BaseTrackingService.onStopTracking()
            }

            // This would be a custom addition, not standard AIDL practice for IPC
            override fun getLocationTrackingManager(): LocationTrackingManager {
                return this@BaseTrackingService.locationTrackingManager // Or this@TripBackgroundService
            }

//        @Throws(RemoteException::class)
//        fun Play(theUrl: String?, theName: String?, theID: String?, isAlarm: Boolean) {
//            this@PlayerService.playUrl(CanonicalAudioEntity(theID, -1, theUrl, theName), isAlarm)
//        }
//        @Throws(RemoteException::class)
//        fun Pause() {
//            this@PlayerService.pause()
//        }

//        @get:Throws(RemoteException::class)
//        val timerSeconds: Long
//            get() = this@PlayerService.getTimerSeconds()
//
//        @get:Throws(RemoteException::class)
//        val currentStationID: String
//            get() = audioEntity.getId()
//
//        @get:Throws(RemoteException::class)
//        val stationName: String
//            get() = audioEntity.getStringName(itsContext)
//
//        @get:Throws(RemoteException::class)
//        val metadataLive: StreamLiveInfo
//            get() = this@PlayerService.liveInfo

            //        @get:Throws(RemoteException::class)
//        val metadataStreamName: String?
//            get() {
//                if (streamInfo != null) return streamInfo.audioName
//                return null
//            }


        }

    //#region constants
    private val YOUR_NOTIFICATION_ID = 1 // Or whatever ID you use
    private val REQUEST_CODE_START = 101
    private val REQUEST_CODE_PAUSE = 102
    private val REQUEST_CODE_RESUME = 103
    private val REQUEST_CODE_STOP = 104
    private val ACTION_START_TRACKING = "com.yourpackage.ACTION_START_TRACKING"
    private val ACTION_PAUSE_TRACKING = "com.yourpackage.ACTION_PAUSE_TRACKING"
    private val ACTION_RESUME_TRACKING = "com.yourpackage.ACTION_RESUME_TRACKING"
    private val ACTION_STOP_TRACKING = "com.yourpackage.ACTION_STOP_TRACKING"

    //#endregion

    //#region tracking functions
    override fun onStartTracking(): Boolean {
        try {
            val result = locationTrackingManager.onStartTracking()
            if (result) {
                updateNotification()
            }
            return result
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onResumeTracking(): Boolean {
        try {
            val result = locationTrackingManager.onResumeTracking()
            if (result) {
                updateNotification()
            }
            return result
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onPauseTracking(): Boolean {
        try {
            val result = locationTrackingManager.onPauseTracking()
            if (result) {
                updateNotification()
            }
            return result
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onStopTracking(): Boolean {
        try {
            val result = locationTrackingManager.onStopTracking()
            if (result) {
                updateNotification()
            }
            return result
        } catch (ex: Exception) {

        }
        return false
    }
    //#endregion

    override fun onBind(intent: Intent): IBinder? {
        Timber.d("Some component want to bind with the service")
        return itsBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand executed with startId: $startId")

        when (intent?.action) {
            ACTION_START_TRACKING -> {
                if (locationTrackingManager.trackingState == TrackingState.IDLE) {
                    if (this@BaseTrackingService.onStartTracking()) {
                        // Start as a foreground service if not already
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForeground(YOUR_NOTIFICATION_ID, createNotification())
                        } else {
                            // For older versions, you might just update the notification
                            updateNotification()
                        }
                    }
                } else if (locationTrackingManager.trackingState == TrackingState.STOPPED) {
                    if (this@BaseTrackingService.onStartTracking()) {
                        // For older versions, you might just update the notification
                        updateNotification()
                    }
                }
            }

            ACTION_PAUSE_TRACKING -> {
                if (locationTrackingManager.trackingState == TrackingState.STARTED) {
                    if (this@BaseTrackingService.onPauseTracking()) {
                        updateNotification() // Update notification to show "Resume"
                    }
                }
            }

            ACTION_RESUME_TRACKING -> {
                if (locationTrackingManager.trackingState == TrackingState.PAUSED) {
                    if (this@BaseTrackingService.onResumeTracking()) {
                        updateNotification() // Update notification to show "Resume"
                    }
                }
            }

            ACTION_STOP_TRACKING -> {
                Timber.d("Tracking STOPPED")
                try {
                    if (this@BaseTrackingService.onStopTracking()) {
                        wakeLock?.let {
                            if (it.isHeld) {
                                it.release()
                            }
                        }
                        stopForeground(true)
                        stopSelf()
                    }
                } catch (e: Exception) {
                    Timber.d("Service stopped without being started: ${e.message}")
                }
                return START_NOT_STICKY // Or as appropriate
            }

            else -> {
                // This is likely the initial start of the service or a restart
                if (locationTrackingManager.trackingState == TrackingState.IDLE) { // Or if it's a restart and you need to resume a state
                    Timber.d("Service starting (initial or restart), current")
                }
            }
        }

        return START_STICKY
    }

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()

        locationTrackingManager.addTrackingLocationListener(listener = this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(YOUR_NOTIFICATION_ID, createNotification())
        } else {
            // For older versions, you might just update the notification
            updateNotification()
        }

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager)
            .run {
                newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "BaseTrackingService::lock"
                )
                    .apply {
//                    acquire(10 * 60 * 1000L /*10 minutes*/)
                        acquire()
                    }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        locationTrackingManager.removeTrackingLocationListener(listener = this)
        Timber.d("The service has been destroyed")
//        wakeLock?.release();
    }

    private fun createAction(
        iconResId: Int,
        title: String,
        intentAction: String,
        requestCode: Int
    ): Notification.Action {
        val intent = Intent(this, this::class.java) // Target this service itself
        intent.action = intentAction
        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getService(this, requestCode, intent, pendingIntentFlags)
        return Notification.Action.Builder(iconResId, title, pendingIntent).build()
    }

    // Method to update the notification
    private fun updateNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Or your min SDK for foreground service
            notificationManager.notify(YOUR_NOTIFICATION_ID, createNotification())
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val context = this
        val restartServiceIntent =
            Intent(context, serviceClassForRestart).also {
                it.setPackage(packageName)
            }
        val restartServicePendingIntent: PendingIntent =
            PendingIntent.getService(
                this, 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        context.getSystemService(Context.ALARM_SERVICE)
        val alarmService: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 1000,
            restartServicePendingIntent
        )
    }

}