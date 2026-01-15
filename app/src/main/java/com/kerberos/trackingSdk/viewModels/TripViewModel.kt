package com.kerberos.trackingSdk.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kerberos.trackingSdk.repositories.repositories.TripPagingRepository
import com.kerberos.trackingSdk.factories.TripUseCaseFactory
import com.kerberos.trackingSdk.mappers.toTrip
import com.kerberos.trackingSdk.models.TripModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TripViewModel(
    private val tripPagingRepository: TripPagingRepository,
    private val tripUseCaseFactory: TripUseCaseFactory,
    private val context: Context
) : ViewModel() {

    val tripList: Flow<PagingData<TripModel>> = tripPagingRepository.getTripPageList()
        .cachedIn(viewModelScope) // Important: caches the paging data in ViewModel lifecycle

    fun importTripsCsv(uri: Uri) {
        viewModelScope.launch {
            val useCase = tripUseCaseFactory.createImportUseCase("csv")
            context.contentResolver.openInputStream(uri)?.let {
                useCase.execute(it)
            }
        }
    }

    fun exportTripsCsv(uri: Uri) {
        viewModelScope.launch {
            val useCase = tripUseCaseFactory.createExportUseCase("csv")
            context.contentResolver.openOutputStream(uri)?.let {
                useCase.execute(it)
            }
        }
    }

    fun importTripsJson(uri: Uri) {
        viewModelScope.launch {
            val useCase = tripUseCaseFactory.createImportUseCase("json")
            context.contentResolver.openInputStream(uri)?.let {
                useCase.execute(it)
            }
        }
    }

    fun exportTripsJson(uri: Uri) {
        viewModelScope.launch {
            val useCase = tripUseCaseFactory.createExportUseCase("json")
            context.contentResolver.openOutputStream(uri)?.let {
                useCase.execute(it)
            }
        }
    }

}