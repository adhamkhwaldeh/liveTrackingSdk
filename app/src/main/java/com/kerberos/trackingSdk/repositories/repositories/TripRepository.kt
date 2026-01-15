package com.kerberos.trackingSdk.repositories.repositories

import com.kerberos.trackingSdk.mappers.toTrip
import com.kerberos.trackingSdk.mappers.toTripModel
import com.kerberos.trackingSdk.mappers.toTripTrack
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.models.TripTrackModel
import com.kerberos.trackingSdk.orm.Trip
import com.kerberos.trackingSdk.orm.TripDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TripRepository(
    private var tripDao: TripDao
) {

    suspend fun getAllTrips(): List<Trip> {
        return tripDao.loadAllData()
    }

    fun getActiveTrip(): Flow<TripModel?> {
        return tripDao.getActiveTrip().map { trip ->
            trip?.toTripModel()
        }
    }

    suspend fun insertTrips(tripTrack: List<TripModel>): Result<Unit> {
        return kotlin.runCatching { tripDao.insert(tripTrack.map { it.toTrip() }) }
    }

    suspend fun createTrip(tripTrack: TripModel): Result<Unit> {
        return kotlin.runCatching { tripDao.insert(tripTrack.toTrip()) }
    }

    suspend fun updateTrip(tripTrack: TripModel): Result<Unit> {
        return kotlin.runCatching { tripDao.update(tripTrack.toTrip()) }
    }

    suspend fun deleteTrip(tripTrack: TripModel): Result<Unit> {
        return kotlin.runCatching { tripDao.delete(tripTrack.toTrip()) }
    }
}
