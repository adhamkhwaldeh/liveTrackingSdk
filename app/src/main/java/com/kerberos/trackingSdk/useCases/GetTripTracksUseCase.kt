package com.kerberos.trackingSdk.useCases

import com.github.adhamkhwaldeh.commonlibrary.base.BaseFlowUseCase
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.models.TripTrackModel
import com.kerberos.trackingSdk.orm.TripTrack
import com.kerberos.trackingSdk.repositories.repositories.TripRepository
import com.kerberos.trackingSdk.repositories.repositories.TripTrackRepository
import kotlinx.coroutines.flow.Flow

class GetTripTracksUseCase(
    private val tripTrackRepository: TripTrackRepository
) : BaseFlowUseCase<List<TripTrackModel>, Int>() {

    override fun invoke(params: Int): Flow<List<TripTrackModel>> {
        return tripTrackRepository.getTripTracks(params)
    }
}