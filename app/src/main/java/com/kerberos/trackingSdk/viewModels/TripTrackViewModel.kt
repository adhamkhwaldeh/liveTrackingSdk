package com.kerberos.trackingSdk.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adhamkhwaldeh.commonlibrary.base.states.BaseState
import com.kerberos.trackingSdk.models.TripTrackModel
import com.kerberos.trackingSdk.orm.TripTrack
import com.kerberos.trackingSdk.repositories.repositories.TripTrackRepository
import com.kerberos.trackingSdk.useCases.AddTripTrackUseCase
import com.kerberos.trackingSdk.useCases.GetTripTracksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class TripTrackViewModel(
    private val addTripTrackUseCase: AddTripTrackUseCase,
    private val getTripTracksUseCase: GetTripTracksUseCase,
) : ViewModel() {

    private val _insertResult = MutableSharedFlow<BaseState<Unit>>()
    val insertResult: SharedFlow<BaseState<Unit>> = _insertResult

    fun addTripTrack(tripTrack: TripTrackModel) {
        viewModelScope.launch {
            addTripTrackUseCase(tripTrack).collectLatest { result ->
                _insertResult.emit(result)
            }
        }
    }


    private val _tripTracks = MutableStateFlow<List<TripTrackModel>>(emptyList())
    val tripTracks: StateFlow<List<TripTrackModel>> = _tripTracks

    fun getTripTracks(tripId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getTripTracksUseCase(tripId).collect {
                _tripTracks.value = it
            }
        }
    }

}