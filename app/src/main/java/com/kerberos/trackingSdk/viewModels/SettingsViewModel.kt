package com.kerberos.trackingSdk.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kerberos.livetrackingsdk.LiveTrackingManager
import com.kerberos.livetrackingsdk.models.SdkSettings
import com.kerberos.trackingSdk.dataStore.AppPrefsStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appPrefsStorage: AppPrefsStorage,
    private val liveTrackingManager: LiveTrackingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        viewModelScope.launch {
            appPrefsStorage.trackSDKConfiguration.collect { settings ->
                settings?.let {
                    liveTrackingManager.changeSdkSettings(it)
                }
            }
        }
    }

    private fun loadSettings() {

        viewModelScope.launch {
            val config = appPrefsStorage.trackSDKConfiguration.first()
            val settings = liveTrackingManager.sdkSettings
            _uiState.value = SettingsUiState(
                locationUpdateInterval = config?.locationUpdateInterval?.toString()
                    ?: settings.locationUpdateInterval.toString(),
                backgroundTrackingEnabled = config?.backgroundTrackingToggle
                    ?: settings.backgroundTrackingToggle,
                minDistance = config?.minDistanceMeters?.toString()
                    ?: settings.minDistanceMeters.toString(),
                language = appPrefsStorage.getLanguage() ?: "English",
                theme = appPrefsStorage.getTheme() ?: "Light"
            )
        }
    }

    fun onLocationUpdateIntervalChanged(interval: String) {
        _uiState.value = _uiState.value.copy(locationUpdateInterval = interval)
    }

    fun onBackgroundTrackingChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(backgroundTrackingEnabled = enabled)
    }

    fun onMinDistanceChanged(distance: String) {
        _uiState.value = _uiState.value.copy(minDistance = distance)
    }

    fun onLanguageChanged(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
    }

    fun onThemeChanged(theme: String) {
        _uiState.value = _uiState.value.copy(theme = theme)
    }

    fun saveSettings() {
        viewModelScope.launch {
            val currentConfig = SdkSettings(
                locationUpdateInterval = _uiState.value.locationUpdateInterval.toLongOrNull()
                    ?: 10000L,
                backgroundTrackingToggle = _uiState.value.backgroundTrackingEnabled,
                minDistanceMeters = _uiState.value.minDistance.toFloatOrNull()
            )
            appPrefsStorage.setTrackSDKConfiguration(currentConfig)
            appPrefsStorage.saveLanguage(_uiState.value.language)
            appPrefsStorage.saveTheme(_uiState.value.theme)
        }
    }
}

data class SettingsUiState(
    val locationUpdateInterval: String = "10000",
    val backgroundTrackingEnabled: Boolean = false,
    val minDistance: String = "25",
    val language: String = "English",
    val theme: String = "Light"
)