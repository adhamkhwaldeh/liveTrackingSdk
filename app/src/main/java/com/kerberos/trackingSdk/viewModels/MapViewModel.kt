package com.kerberos.trackingSdk.viewModels

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.kerberos.livetrackingsdk.interfaces.ITrackingLocationListener
import com.kerberos.livetrackingsdk.managers.LocationTrackingManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel(private val locationTrackingManager: LocationTrackingManager) : ViewModel(),
    ITrackingLocationListener {

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    fun startLocationUpdates() {
        locationTrackingManager.addTrackingLocationListener(this)
        locationTrackingManager.onStartTracking()
    }

    fun stopLocationUpdates() {
        locationTrackingManager.onStopTracking()
    }

    override fun onLocationUpdated(currentLocation: Location?) {
        currentLocation?.let {
            _currentLocation.value = LatLng(it.latitude, it.longitude)
        }
    }

    override fun onLocationUpdateFailed(exception: Exception) {
        // Handle location update failure
    }

}