package com.kerberos.trackingSdk.ui.trip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kerberos.trackingSdk.helpers.formatDuration
import com.kerberos.trackingSdk.helpers.toFormattedDate
import com.kerberos.trackingSdk.models.TripModel

@Composable
fun TripItem(trip: TripModel, onDelete: (trip: TripModel) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Trip ID: ${trip.id}", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.weight(1f)) // pushes the next item to the end
                IconButton(
                    onClick = {
                        onDelete(trip)
                    }
                ) { Icon(Icons.Filled.Delete, contentDescription = "Delete") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(text = "Start Time: ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = trip.startTime.toFormattedDate(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (trip.endTime != null) {
                Row {
                    Text(text = "End Time: ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = trip.endTime!!.toFormattedDate(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row {
                    Text(text = "Duration: ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "${trip.tripDuration.formatDuration()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f)) // pushes the next item to the end
                Text(
                    text = "Points: ${trip.totalPoints}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                Text(text = "Distance: ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = String.format("%.2f Meter", trip.totalDistance),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (trip.isActive) "Status: Active" else "Status: Inactive",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


