package com.kerberos.trackingSdk.useCases

import com.github.adhamkhwaldeh.commonlibrary.base.BaseFlowUseCase
import com.github.adhamkhwaldeh.commonlibrary.base.BaseSealedUseCase
import com.github.adhamkhwaldeh.commonlibrary.base.states.BaseState
import com.github.adhamkhwaldeh.commonlibrary.base.states.asBasState
import com.kerberos.trackingSdk.models.TripTrackModel
import com.kerberos.trackingSdk.repositories.repositories.TripTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddTripTrackUseCase(
    private val tripTrackRepository: TripTrackRepository
) : BaseSealedUseCase<Unit, TripTrackModel>() {
    override suspend fun invoke(params: TripTrackModel): Flow<BaseState<Unit>> {
        return flow {
            emit(tripTrackRepository.createTripTrack(params).asBasState())
        }
    }

}