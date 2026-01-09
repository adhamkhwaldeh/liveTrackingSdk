package com.kerberos.trackingSdk.useCases

import com.github.adhamkhwaldeh.commonlibrary.base.BaseSealedUseCase
import com.github.adhamkhwaldeh.commonlibrary.base.states.BaseState
import com.github.adhamkhwaldeh.commonlibrary.base.states.asBasState
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.repositories.repositories.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteTripUseCase(
    private val tripRepository: TripRepository
) : BaseSealedUseCase<Unit, TripModel>() {

    override suspend fun invoke(params: TripModel): Flow<BaseState<Unit>> {
        return flow {
            emit(tripRepository.deleteTrip(params).asBasState())
        }
    }

}
