package com.kerberos.livetrackingsdk.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.kerberos.livetrackingsdk.models.SdkSettings // Assuming you still use this data class

class SdkPreferencesManager(context: Context) {

    private val appContext = context.applicationContext
    private val sharedPreferences: SharedPreferences

    // Define Preference Keys
    private object PreferencesKeys {
        const val LOCATION_UPDATE_INTERVAL = "location_update_interval"
        const val LOCATION_UPDATE_DISTANCE = "location_update_distance"
        const val BACKGROUND_TRACKING_TOGGLE = "background_tracking_toggle"
    }

    // Define a name for your SharedPreferences file
    companion object {
        private const val PREFS_NAME = "kerberos_sdk_settings_prefs"
        const val DEFAULT_LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
        const val DEFAULT_MIN_DISTANCE_METERS = 25f // 5 seconds


        const val DEFAULT_BACKGROUND_TRACKING_TOGGLE = true
    }

    init {
        sharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Reads the current SDK settings.
     * Uses default values if settings are not yet stored.
     */
    fun getSettings(): SdkSettings {
        val locationInterval = sharedPreferences.getLong(
            PreferencesKeys.LOCATION_UPDATE_INTERVAL,
            DEFAULT_LOCATION_UPDATE_INTERVAL
        )
        val minDistanceMeters = if (sharedPreferences.contains(PreferencesKeys.LOCATION_UPDATE_DISTANCE)) {
            sharedPreferences.getFloat(PreferencesKeys.LOCATION_UPDATE_DISTANCE, DEFAULT_MIN_DISTANCE_METERS)
        } else {
            null
        }
        val backgroundToggle = sharedPreferences.getBoolean(
            PreferencesKeys.BACKGROUND_TRACKING_TOGGLE,
            DEFAULT_BACKGROUND_TRACKING_TOGGLE
        )
        return SdkSettings(locationInterval, minDistanceMeters, backgroundToggle)
    }

    /**
     * Updates the location update interval.
     */
    fun updateLocationUpdateInterval(interval: Long) {
        sharedPreferences.edit {
            putLong(PreferencesKeys.LOCATION_UPDATE_INTERVAL, interval)
            // apply() // edit() with Kotlin extension already calls apply()
        }
    }

    /**
     * Updates the background tracking toggle.
     */
    fun updateBackgroundTrackingToggle(isEnabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(PreferencesKeys.BACKGROUND_TRACKING_TOGGLE, isEnabled)
        }
    }

    /**
     * Updates all settings at once.
     */
    fun updateAllSettings(newSettings: SdkSettings) {
        sharedPreferences.edit {
            putLong(PreferencesKeys.LOCATION_UPDATE_INTERVAL, newSettings.locationUpdateInterval)
            newSettings.minDistanceMeters?.let {
                putFloat(PreferencesKeys.LOCATION_UPDATE_DISTANCE, it)
            } ?: remove(PreferencesKeys.LOCATION_UPDATE_DISTANCE) // Remove if null
            putBoolean(
                PreferencesKeys.BACKGROUND_TRACKING_TOGGLE,
                newSettings.backgroundTrackingToggle
            )
        }
    }

    /**
     * Clears all SDK settings from SharedPreferences.
     * Useful for testing or reset functionality.
     */
    fun clearAllSettings() {
        sharedPreferences.edit {
            clear()
        }
    }
}