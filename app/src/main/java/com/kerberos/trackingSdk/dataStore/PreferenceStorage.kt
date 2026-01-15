package com.kerberos.trackingSdk.dataStore

import com.kerberos.livetrackingsdk.models.SdkSettings
import kotlinx.coroutines.flow.Flow

interface PreferenceStorage {

    val trackSDKConfiguration: Flow<SdkSettings?>
    suspend fun setTrackSDKConfiguration(configuration: SdkSettings)

    suspend fun getLanguage(): String?
    suspend fun saveLanguage(language: String)

    suspend fun getTheme(): String?
    fun getThemeFlow(): Flow<String?>
    suspend fun saveTheme(theme: String)

    /***
     * clears all the stored data
     */

    suspend fun clearPreferenceStorage()
}