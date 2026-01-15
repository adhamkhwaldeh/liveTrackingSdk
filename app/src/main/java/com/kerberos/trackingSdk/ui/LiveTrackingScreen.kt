package com.kerberos.trackingSdk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kerberos.livetrackingsdk.enums.TrackingState
import com.kerberos.trackingSdk.viewModels.LiveTrackingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LiveTrackingScreen(
    viewModel: LiveTrackingViewModel = koinViewModel(),
) {
    val trackingState by viewModel.trackingState.collectAsState()
    val locationState by viewModel.locationState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Tracking State: ${trackingState.name}")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Location: ${locationState?.latitude}, ${locationState?.longitude}")
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Button(
                onClick = { viewModel.startTracking() },
                enabled = TrackingState.startable(trackingState)
            ) {
                Text("Start")
            }
            Button(
                onClick = { viewModel.stopTracking() },
                enabled = TrackingState.stoppable(trackingState)
            ) {
                Text("Stop")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = { viewModel.pauseTracking() },
                enabled = trackingState == TrackingState.STARTED
            ) {
                Text("Pause")
            }
            Button(
                onClick = { viewModel.resumeTracking() },
                enabled = trackingState == TrackingState.PAUSED
            ) {
                Text("Resume")
            }
        }
    }
}
