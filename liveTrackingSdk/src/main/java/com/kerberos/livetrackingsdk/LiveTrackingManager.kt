package com.kerberos.livetrackingsdk

import android.content.Context
import com.kerberos.livetrackingsdk.repositories.SdkPreferencesManager
import com.kerberos.livetrackingsdk.enums.LiveTrackingMode
import com.kerberos.livetrackingsdk.interfaces.ITrackingStatusListener
import com.kerberos.livetrackingsdk.interfaces.ITrackingActionsListener
import com.kerberos.livetrackingsdk.interfaces.ITrackingLocationListener
import com.kerberos.livetrackingsdk.interfaces.ITrackingSdkModeStatusListener
import com.kerberos.livetrackingsdk.managers.LocationTrackingManager
import com.kerberos.livetrackingsdk.models.SdkSettings
import com.kerberos.livetrackingsdk.services.BaseTrackingService
import com.kerberos.livetrackingsdk.services.DefaultTrackingService
import com.kerberos.livetrackingsdk.managers.BackgroundTrackingManager
import com.kerberos.livetrackingsdk.managers.BaseTrackingManager
import com.adham.commonsdk.basesdk.BaseSDK
import com.kerberos.livetrackingsdk.managers.ForegroundTrackingManager


class LiveTrackingManager private constructor(
    val context: Context,
    backgroundService: Class<out BaseTrackingService> = DefaultTrackingService::class.java,
    var liveTrackingMode: LiveTrackingMode = LiveTrackingMode.FOREGROUND_SERVICE,
    val sdkPreferencesManager: SdkPreferencesManager,
    private val trackingLocationListener: MutableList<ITrackingStatusListener> = mutableListOf()
) : BaseSDK(context), ITrackingActionsListener, ITrackingSdkModeStatusListener {

    // Builder Class
    class Builder(private val applicationContext: Context) {
        // Default values are set here

        private var backgroundService: Class<out BaseTrackingService> =
            DefaultTrackingService::class.java

        private var trackingMode: LiveTrackingMode = LiveTrackingMode.FOREGROUND_SERVICE

        // For SdkSettings, we can either take individual params or an SdkSettings object
        // Option 1: Individual params for SdkSettings
        private var minDistance: Float? =
            SdkPreferencesManager.DEFAULT_MIN_DISTANCE_METERS // Use defaults from manager
        private var updateInterval: Long = SdkPreferencesManager.DEFAULT_LOCATION_UPDATE_INTERVAL

        // Option 2: SdkSettings object (can be combined with Option 1 for overrides)
        private var customSdkSettings: SdkSettings? = null

        // Initialize SdkPreferencesManager here to load persisted values for defaults
        private val preferencesManager = SdkPreferencesManager(applicationContext)


        init {
            // Load persisted settings to override compile-time defaults for the builder
            val persistedSettings =
                preferencesManager.getSettings() // Synchronous for SharedPreferences
            this.minDistance = persistedSettings.minDistanceMeters
            this.updateInterval = persistedSettings.locationUpdateInterval
            this.trackingMode =
                if (persistedSettings.backgroundTrackingToggle) LiveTrackingMode.BACKGROUND_SERVICE else LiveTrackingMode.FOREGROUND_SERVICE
        }


        fun setBackgroundService(serviceClass: Class<out BaseTrackingService>): Builder =
            apply { // Or Service
                this.backgroundService = serviceClass
            }

        fun setLiveTrackingMode(mode: LiveTrackingMode): Builder = apply {
            this.trackingMode = mode
        }

        // --- Methods for SdkSettings ---
        // Option 1: Set individual SdkSettings parameters
        fun setMinDistanceMeters(distance: Float): Builder = apply {
            this.minDistance = distance
        }

        fun setLocationUpdateInterval(interval: Long): Builder = apply {
            this.updateInterval = interval
        }

        // Option 2: Allow providing a full SdkSettings object
        fun setSdkSettings(settings: SdkSettings): Builder = apply {
            this.customSdkSettings = settings
            // Override individual params if a full object is given
            this.minDistance = settings.minDistanceMeters
            this.updateInterval = settings.locationUpdateInterval
        }


        fun build(): LiveTrackingManager {
            val finalSdkSettings = customSdkSettings ?: SdkSettings(
                minDistanceMeters = this.minDistance,
                locationUpdateInterval = this.updateInterval,
                backgroundTrackingToggle = this.trackingMode == LiveTrackingMode.BACKGROUND_SERVICE
            )

            // Important: Persist the final settings if they were set via the builder
            // This ensures that the settings used for this session are saved for the next.
            // Only update if they differ from what's already persisted to avoid unnecessary writes.
            val currentlyPersisted = preferencesManager.getSettings()
            if (finalSdkSettings != currentlyPersisted) {
                // If using SharedPreferences, this is synchronous.
                // If using DataStore, this would need to be handled in a coroutine.
                preferencesManager.updateAllSettings(finalSdkSettings)
            }


            val sdkInstance = LiveTrackingManager(
                context = applicationContext,
                backgroundService = this.backgroundService,
                liveTrackingMode = this.trackingMode, // Use the mode set in the builder
                sdkPreferencesManager = this.preferencesManager
            )
            // KerberosLiveTracking.INSTANCE = sdkInstance // Set the singleton instance
            return sdkInstance
        }
    }

    val trackingManagers: MutableList<BaseTrackingManager> = mutableListOf(
        ForegroundTrackingManager(context, this),
        BackgroundTrackingManager(context, backgroundService, this,)
    )

    val trackingLocationListeners: MutableList<ITrackingLocationListener> = mutableListOf()

    val trackingStateListeners: MutableList<ITrackingStatusListener> = mutableListOf()

    val currentTrackingManager: BaseTrackingManager
        get() {
            return when (liveTrackingMode) {
                LiveTrackingMode.FOREGROUND_SERVICE -> trackingManagers.first { it is ForegroundTrackingManager }
                LiveTrackingMode.BACKGROUND_SERVICE -> trackingManagers.first { it is BackgroundTrackingManager }
            }
        }

    val sdkSettings: SdkSettings
        get() {
            return sdkPreferencesManager.getSettings()
        }

    var currentLocationTrackingManager: LocationTrackingManager? = null

    override fun onTrackingSDKModeInitialized(
        locationTrackingManager: LocationTrackingManager, liveTrackingMode: LiveTrackingMode
    ) {
        locationTrackingManager.addTrackingLocationListener(trackingLocationListeners)
        locationTrackingManager.trackingStateListeners = trackingStateListeners

        currentLocationTrackingManager = locationTrackingManager

    }

    fun changeTrackingMode(newMode: LiveTrackingMode): Boolean {
        if (newMode == liveTrackingMode) return true
        currentLocationTrackingManager?.resetCurrentTrackStateAfterChangeMode()

        val stopResult = currentTrackingManager.destroyTrackingManager() ?: false
        if (!stopResult) return false
//        trackingLocationListener.removeIf{x->}
//        clearAllTrackingLocationListeners()
//        clearAllTrackingStatusListeners()
        liveTrackingMode = newMode
        val initResult = currentTrackingManager.initializeTrackingManager()
        if (!initResult) return false
        return true
    }

    fun changeSdkSettings(sdkSettings: SdkSettings): Boolean {
        changeTrackingMode(
            if (sdkSettings.backgroundTrackingToggle) LiveTrackingMode.BACKGROUND_SERVICE else LiveTrackingMode.FOREGROUND_SERVICE
        )
        sdkPreferencesManager.updateAllSettings(sdkSettings)
        return currentLocationTrackingManager?.invalidateConfiguration() ?: false
    }

    //#region Tracking location Listener

    /**
     * Adds a tracking location interface to receive location updates.
     *
     * @param listener The interface to add.
     */
    fun addTrackingLocationListener(listener: ITrackingLocationListener) {
        if (!trackingLocationListeners.contains(listener)) {
            trackingLocationListeners.add(listener)
        }
    }

    /**
     * Removes a tracking location interface from receiving location updates.
     *
     * @param listener The interface to remove.
     */
    fun removeTrackingLocationListener(listener: ITrackingLocationListener) {
        trackingLocationListeners.remove(listener)
    }

    /**
     * Clears all registered tracking location interfaces.
     */
    fun clearAllTrackingLocationListeners() {
        trackingLocationListeners.clear()
    }
    //#endregion

    //#region Tracking status Listener
    fun addTrackingStatusListener(listener: ITrackingStatusListener) {
        if (!trackingStateListeners.contains(listener)) {
            trackingStateListeners.add(listener)
        }
    }

    /**
     * Removes a tracking location interface from receiving location updates.
     *
     * @param listener The interface to remove.
     */
    fun removeTrackingStatusListener(listener: ITrackingStatusListener) {
        trackingStateListeners.remove(listener)
    }

    fun clearAllTrackingStatusListeners() {
        trackingStateListeners.clear()
    }
    //#endregion

    //#region Tracking Actions Listener
    override fun onStartTracking(): Boolean {
        try {
            val result = currentTrackingManager?.onStartTracking() ?: false
            return result
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onResumeTracking(): Boolean {
        try {
            val result = currentTrackingManager?.onResumeTracking() ?: false
            return result
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onPauseTracking(): Boolean {
        try {
            val result = currentTrackingManager?.onPauseTracking() ?: false
            return result
        } catch (ex: Exception) {

        }
        return false
    }

    override fun onStopTracking(): Boolean {
        try {
            val result = currentTrackingManager?.onStopTracking() ?: false
            return result
        } catch (ex: Exception) {

        }
        return false
    }
    //#endregion

}
