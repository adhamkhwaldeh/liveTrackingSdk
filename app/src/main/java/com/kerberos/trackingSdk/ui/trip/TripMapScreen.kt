package com.kerberos.trackingSdk.ui.trip

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.kerberos.trackingSdk.viewModels.MapViewModel
import com.kerberos.trackingSdk.viewModels.TripTrackViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TripMapScreen(
    viewModel: MapViewModel = koinViewModel(),
    tripTrackViewModel: TripTrackViewModel = koinViewModel(),
) {
    val tripTracks by tripTrackViewModel.tripTracks.collectAsState()

    val currentLocation by viewModel.currentLocation.collectAsState()
    var cameraInitialized by remember { mutableStateOf(false) }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }

    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    LaunchedEffect(locationPermissionState.permission) {
        if (locationPermissionState.status == PermissionStatus.Granted) {
            viewModel.startLocationUpdates()
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopLocationUpdates()
        }
    }

    LaunchedEffect(currentLocation) {
        if (currentLocation != null && !cameraInitialized) {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(
                    currentLocation!!,
                    15f
                )
            cameraInitialized = true
            viewModel.stopLocationUpdates()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            properties = MapProperties(
                isMyLocationEnabled = currentLocation != null
            ),
            cameraPositionState = cameraPositionState
        ) {
            if (tripTracks.size > 1) {
                Polyline(points = tripTracks.map { LatLng(it.latitude, it.longitude) })
            }
            currentLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "You are here"
                )
            }
        }
        TripControls(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

}
