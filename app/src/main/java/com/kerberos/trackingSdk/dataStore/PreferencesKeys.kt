package com.kerberos.trackingSdk.dataStore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val TRACK_SDK_CONFIGURATION = stringPreferencesKey("TrackSDKConfiguration")
    val LANGUAGE = stringPreferencesKey("Language")
    val THEME = stringPreferencesKey("Theme")
}