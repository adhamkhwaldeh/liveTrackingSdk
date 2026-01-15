package com.kerberos.trackingSdk.repositories.repositories


import com.kerberos.trackingSdk.mappers.toTripTrack
import com.kerberos.trackingSdk.mappers.toTripTrackModel
import com.kerberos.trackingSdk.models.TripTrackModel
import com.kerberos.trackingSdk.orm.TripTrack
import com.kerberos.trackingSdk.orm.TripTrackDoa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TripTrackRepository(private val tripTrackDao: TripTrackDoa) {

    fun getTripTracks(tripId: Int): Flow<List<TripTrackModel>> = flow {
        emit(tripTrackDao.loadAllData().filter { it.tripId == tripId }
            .map { it.toTripTrackModel() })
    }

    suspend fun createTripTrack(tripTrack: TripTrackModel): Result<Unit> {
        return kotlin.runCatching { tripTrackDao.addPointAndUpdateTrip(tripTrack.toTripTrack()) }
    }

}
