package com.kerberos.trackingSdk.ui.trip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kerberos.livetrackingsdk.enums.TrackingState
import com.kerberos.trackingSdk.enums.TripStatus
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.models.TripTrackModel
import com.kerberos.trackingSdk.viewModels.LiveTrackingViewModel
import com.kerberos.trackingSdk.viewModels.TripTrackViewModel
import com.kerberos.trackingSdk.viewModels.TripManageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TripControls(
    modifier: Modifier = Modifier,

    viewModel: TripManageViewModel = koinViewModel(),

    tripTrackViewModel: TripTrackViewModel = koinViewModel(),

    liveTrackingViewModel: LiveTrackingViewModel = koinViewModel()
) {
    val activeTrip = viewModel.activeTrip.collectAsState()//.collectAsState()
    var tripStatus by remember { mutableStateOf(TripStatus.STOPPED) }

    val trackingState by liveTrackingViewModel.trackingState.collectAsState()

    LaunchedEffect(activeTrip, activeTrip.value) {
        tripStatus = if (activeTrip.value != null) {
            if (activeTrip.value!!.isActive) {
                TripStatus.RUNNING
            } else {
                TripStatus.PAUSED
            }
        } else {
            TripStatus.STOPPED
        }
    }

    LaunchedEffect(tripStatus) {
        when (tripStatus) {
            TripStatus.STOPPED -> {
                if (TrackingState.stoppable(trackingState)) {
                    liveTrackingViewModel.stopTracking()
                }
            }

            TripStatus.RUNNING -> {
                if (TrackingState.startable(trackingState)) {
                    liveTrackingViewModel.startTracking()
                } else if (trackingState == TrackingState.PAUSED) {
                    liveTrackingViewModel.resumeTracking()
                }
            }

            TripStatus.PAUSED -> {
                if (trackingState == TrackingState.STARTED) {
                    liveTrackingViewModel.pauseTracking()
                } else if (TrackingState.startable(trackingState)) {
                    liveTrackingViewModel.startTracking()
                }
            }
        }
    }

    LaunchedEffect(trackingState) {

        when (trackingState) {
            TrackingState.IDLE -> {

            }

            TrackingState.STARTED -> {
                if (tripStatus == TripStatus.PAUSED && activeTrip.value != null) {
                    viewModel.updateTrip(
                        trip = activeTrip.value!!.copy(isActive = true)
                    )
                } else if (tripStatus == TripStatus.STOPPED) {
                    viewModel.addTrip(
                        TripModel(
                            startTime = System.currentTimeMillis(),
                            isActive = true,
                        )
                    )
                }
            }

            TrackingState.PAUSED -> {
                if (tripStatus == TripStatus.RUNNING && activeTrip.value != null) {
                    viewModel.updateTrip(
                        trip = activeTrip.value!!.copy(isActive = false)
                    )
                } else if (tripStatus == TripStatus.STOPPED) {
                    viewModel.addTrip(
                        TripModel(
                            startTime = System.currentTimeMillis(),
                            isActive = false,
                        )
                    )
                }
            }

            TrackingState.STOPPED -> {
                if (tripStatus != TripStatus.STOPPED && activeTrip.value != null) {
                    viewModel.updateTrip(
                        trip = activeTrip.value!!.copy(
                            isActive = false, endTime = System.currentTimeMillis(),
                        )
                    )
                }
            }

        }
    }

    val locationState by liveTrackingViewModel.locationState.collectAsState()

    LaunchedEffect(locationState) {
        if (locationState != null && activeTrip.value != null) {
            if (tripStatus == TripStatus.RUNNING) {
                tripTrackViewModel.addTripTrack(
                    TripTrackModel(
                        id = 0,
                        tripId = activeTrip.value!!.id,
                        latitude = locationState!!.latitude,
                        longitude = locationState!!.longitude,
                        speed = locationState!!.speed.toDouble(),
                        timestamp = System.currentTimeMillis(),
                        altitude = locationState!!.altitude,
                        accuracy = locationState!!.accuracy.toDouble(),
                        bearing = locationState!!.bearing.toDouble()
                    )
                )
            }
        }
    }

    TripItemForMap(activeTrip.value)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Button(onClick = {
            viewModel.addTrip(
                TripModel(
                    startTime = System.currentTimeMillis(),
                    isActive = true,
                )
            )
        }, enabled = tripStatus == TripStatus.STOPPED) {
            Text("Start")
        }

        Button(onClick = {
            viewModel.updateTrip(
                activeTrip.value!!.copy(
                    isActive = true,
                )
            )
        }, enabled = tripStatus == TripStatus.PAUSED) {
            Text("Resume")
        }

        Button(onClick = {
            viewModel.updateTrip(
                activeTrip.value!!.copy(
                    isActive = false,
                )
            )
        }, enabled = tripStatus == TripStatus.RUNNING) {
            Text("Pause")
        }
        Button(onClick = {
            viewModel.updateTrip(
                trip = activeTrip.value!!.copy(
                    isActive = false, endTime = System.currentTimeMillis(),
                )
            )
        }, enabled = TripStatus.isStoppable(tripStatus)) {
            Text("Stop")
        }

    }
}