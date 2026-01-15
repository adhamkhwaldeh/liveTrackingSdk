package com.kerberos.trackingSdk.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.adhamkhwaldeh.commonlibrary.base.states.BaseState
import com.kerberos.trackingSdk.mappers.toTrip
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.useCases.AddNewTripUseCase
import com.kerberos.trackingSdk.useCases.DeleteTripUseCase
import com.kerberos.trackingSdk.useCases.GetCurrentTripUseCase
import com.kerberos.trackingSdk.useCases.UpdateTripUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripManageViewModel(
    private val currentTripUseCase: GetCurrentTripUseCase,
    private val addTripUseCase: AddNewTripUseCase,
    private val updateTripUseCase: UpdateTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
//    private val context: Context
) : ViewModel() {

    val activeTrip: StateFlow<TripModel?> = currentTripUseCase(null)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _insertResult = MutableSharedFlow<BaseState<Unit>>()
    val insertResult: SharedFlow<BaseState<Unit>> = _insertResult

    fun addTrip(trip: TripModel) {
        viewModelScope.launch(Dispatchers.IO) {
            addTripUseCase(trip).collectLatest { result ->
                _insertResult.emit(result)
            }
        }
    }


    private val _updateResult = MutableSharedFlow<BaseState<Unit>>()
    val updateResult: SharedFlow<BaseState<Unit>> = _updateResult

    fun updateTrip(trip: TripModel) {
        viewModelScope.launch(Dispatchers.IO) {
            updateTripUseCase(trip).collectLatest { result ->
                _updateResult.emit(result)
            }
        }
    }

    private val _deleteResult = MutableSharedFlow<BaseState<Unit>>()
    val deleteResult: SharedFlow<BaseState<Unit>> = _deleteResult

    fun deleteTrip(trip: TripModel) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTripUseCase(trip).collectLatest { result ->
                _updateResult.emit(result)
            }
        }
    }

}