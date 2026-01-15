package com.kerberos.trackingSdk.useCases

import com.github.adhamkhwaldeh.commonlibrary.base.BaseFlowUseCase
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.repositories.repositories.TripRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentTripUseCase(
    private val tripRepository: TripRepository
) : BaseFlowUseCase<TripModel?, Void?>() {

    override fun invoke(params: Void?): Flow<TripModel?> {
        return tripRepository.getActiveTrip()
    }
}