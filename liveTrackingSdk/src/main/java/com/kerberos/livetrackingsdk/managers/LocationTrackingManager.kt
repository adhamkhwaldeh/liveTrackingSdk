package com.kerberos.livetrackingsdk.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kerberos.livetrackingsdk.repositories.SdkPreferencesManager
import com.kerberos.livetrackingsdk.interfaces.ITrackingLocationListener
import com.kerberos.livetrackingsdk.interfaces.ITrackingActionsListener
import com.kerberos.livetrackingsdk.enums.TrackingState
import com.kerberos.livetrackingsdk.exceptions.GpsNotEnabledException
import com.kerberos.livetrackingsdk.exceptions.PermissionNotGrantedException
import com.kerberos.livetrackingsdk.helpers.PermissionsHelper
import com.kerberos.livetrackingsdk.interfaces.ITrackingStatusListener
import timber.log.Timber
import kotlin.properties.Delegates

class LocationTrackingManager(
    val context: Context,
) : ITrackingActionsListener {

    private val fusedClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val sdkPreferencesManager: SdkPreferencesManager by lazy {
        SdkPreferencesManager(context)
    }

    private val request: LocationRequest
        get() {

            val minTimeMillis = sdkPreferencesManager.getSettings().locationUpdateInterval
            val minDistanceMeters = sdkPreferencesManager.getSettings().minDistanceMeters
            Log.e("minTimeMillis", "minTimeMillis $minTimeMillis")
//            Timber.d("minTimeMillis", "minTimeMillis $minTimeMillis")
            var builder = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                minTimeMillis.coerceAtLeast(1000L),
            )
                .setWaitForAccurateLocation(false)
                .setMaxUpdateAgeMillis(minTimeMillis)
                // Ensure interval isn't too small
                // .setPriority(Priority.PRIORITY_HIGH_ACCURACY) // Already set in constructor
                .setIntervalMillis(minTimeMillis)
                .setMinUpdateIntervalMillis(minTimeMillis) // Smallest interval if updates are more frequent from other sources

            if (minDistanceMeters != null) {
                builder = builder.setMinUpdateDistanceMeters(minDistanceMeters)
                // .setMaxUpdates(1) // Remove if you want continuous updates
                // Use the values set by the builder
            }

            return builder.build()
        }

    private var trackingLocationListeners: MutableList<ITrackingLocationListener> =
        mutableListOf()

    var trackingStateListeners: MutableList<ITrackingStatusListener> =
        mutableListOf()

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            trackingLocationListeners.forEach { trackingLocationInterface ->
                trackingLocationInterface.onLocationUpdated(location)
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability) {
            if (!availability.isLocationAvailable) {
                trackingLocationListeners.toList().forEach { trackingLocationInterface ->
                    val gpsValue = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    if (!gpsValue) {
                        trackingLocationInterface.onLocationUpdateFailed(GpsNotEnabledException())
                        return@forEach
                    } else if (!PermissionsHelper.isPermissionGranted(context)) {
                        trackingLocationInterface.onLocationUpdateFailed(
                            PermissionNotGrantedException()
                        )

                    } else {
                        // You might want a specific callback for unavailability or use onLocationUpdateFailed
                        trackingLocationInterface.onLocationUpdateFailed(Exception("Location became unavailable"))
                    }
                }
            }
        }
    }

    private var currentTrackingState: TrackingState by Delegates.observable(TrackingState.IDLE) { property, oldValue, newValue ->
        Timber.d(
            "LocationTrackingManager",
            "TrackingState changed from $oldValue to $newValue (property: ${property.name})"
        )
        // --- Actions to take on state change ---

        // Example: Notify external listeners about state change
        trackingStateListeners.toList().forEach { it.onTrackingStateChanged(newValue) }

        // Example: Update notification if this manager handles it
        // if (newValue == TrackingState.STARTED || newValue == TrackingState.PAUSED) {
        //     updateNotification() // You'd need a method to update/show notification
        // } else if (newValue == TrackingState.IDLE || newValue == TrackingState.STOPPED) {
        //     removeNotification()
        // }

        // Example: Specific logic for transitions
        when (newValue) {
            TrackingState.STARTED -> {
                // Logic specific to entering STARTED state
            }

            TrackingState.PAUSED -> {
                // Logic specific to entering PAUSED state
            }
            // ... other states
            else -> {}
        }
    }

    fun resetCurrentTrackStateAfterChangeMode() {
        currentTrackingState = TrackingState.IDLE
    }

    val trackingState: TrackingState
        get() {
            return currentTrackingState
        }

    fun addTrackingLocationListener(listeners: MutableList<ITrackingLocationListener>) {
        trackingLocationListeners.forEach { listener ->
            if (!listeners.contains(listener)) {
                listeners.add(listener)
            }
        }
        trackingLocationListeners = listeners
    }

    fun addTrackingLocationListener(listener: ITrackingLocationListener) {
        if (!trackingLocationListeners.contains(listener)) {
            trackingLocationListeners.add(listener)
        }
    }

    fun removeTrackingLocationListener(listener: ITrackingLocationListener) {
        trackingLocationListeners.remove(listener)
    }

    //#region Tracking actions Listener
    override fun onStartTracking(): Boolean {
        if (!PermissionsHelper.isPermissionGranted(context)) {
            Timber.w("Location permission not granted for starting tracking.")
            trackingLocationListeners.forEach { trackingLocationInterface ->
                trackingLocationInterface.onLocationUpdateFailed(PermissionNotGrantedException())
            }
            return false
        }

        try {
            // Request an immediate location update
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Timber.d("Successfully obtained immediate location: $location")
                        trackingLocationListeners.forEach { listener ->
                            listener.onLocationUpdated(location)
                        }
                    } else {
                        Timber.d("Immediate location via getCurrentLocation was null (e.g., GPS off or unavailable).")
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Failed to get immediate current location.")
                    // Not calling onLocationUpdateFailed here as regular updates might still work,
                    // or onLocationAvailability will report issues like GPS disabled.
                }

            // Start regular location updates
            fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
            currentTrackingState = TrackingState.STARTED
            Timber.i("Location tracking successfully initiated with regular updates.")
            return true

        } catch (secEx: SecurityException) {
            Timber.e(secEx, "SecurityException in onStartTracking. Permissions may have been revoked or are missing.")
            trackingLocationListeners.forEach { trackingLocationInterface ->
                trackingLocationInterface.onLocationUpdateFailed(PermissionNotGrantedException("SecurityException: ${secEx.message}"))
            }
            return false
        } catch (ex: Exception) { // Catching a broader exception for unexpected issues
            Timber.e(ex, "Unexpected exception in onStartTracking.")
            trackingLocationListeners.forEach { trackingLocationInterface ->
                trackingLocationInterface.onLocationUpdateFailed(ex)
            }
            return false
        }
    }

    override fun onResumeTracking(): Boolean {
        return onStartTracking()
    }

    override fun onPauseTracking(): Boolean {
        fusedClient.removeLocationUpdates(callback)
        currentTrackingState = TrackingState.PAUSED
        return true
    }

    override fun onStopTracking(): Boolean {
        currentTrackingState = TrackingState.STOPPED
        fusedClient.removeLocationUpdates(callback)
        return true
    }
    //#endregion

    /**
     * Invalidates the current configuration by stopping and restarting the tracking.
     * This is useful when settings have changed and you want to apply the new configuration.
     *
     * @return True if both stopping and starting tracking were successful, false otherwise.
     */
    fun invalidateConfiguration(): Boolean {
        if (trackingState == TrackingState.STARTED) {
            return onStopTracking() && onStartTracking()
        }
        return true
    }

}
