package com.kerberos.trackingSdk.di

import android.app.Application
import com.kerberos.trackingSdk.repositories.repositories.TripPagingRepository
import com.google.gson.Gson
import com.kerberos.livetrackingsdk.LiveTrackingManager
import com.kerberos.trackingSdk.factories.TripUseCaseFactory
import com.kerberos.trackingSdk.orm.LiveTrackingDatabase
import com.kerberos.trackingSdk.repositories.repositories.TripPagingRepositoryImpl
import com.kerberos.trackingSdk.repositories.repositories.TripRepository
import com.kerberos.trackingSdk.repositories.repositories.TripTrackRepository
import com.kerberos.trackingSdk.viewModels.LiveTrackingViewModel
import com.kerberos.trackingSdk.viewModels.SettingsViewModel
import com.kerberos.trackingSdk.useCases.AddTripTrackUseCase
import com.kerberos.trackingSdk.useCases.AddNewTripUseCase
import com.kerberos.trackingSdk.viewModels.TripTrackViewModel
import com.kerberos.trackingSdk.viewModels.TripViewModel
import com.kerberos.livetrackingsdk.managers.LocationTrackingManager
import com.kerberos.trackingSdk.TripBackgroundService
import com.kerberos.trackingSdk.dataStore.AppPrefsStorage
import com.kerberos.trackingSdk.useCases.DeleteTripUseCase
import com.kerberos.trackingSdk.useCases.GetCurrentTripUseCase
import com.kerberos.trackingSdk.useCases.GetTripTracksUseCase
import com.kerberos.trackingSdk.useCases.UpdateTripUseCase
import com.kerberos.trackingSdk.viewModels.MapViewModel
import com.kerberos.trackingSdk.viewModels.TripManageViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinStarter {

    fun startKoin(app: Application) {
        startKoin {
            androidLogger()
            androidContext(app)
            modules(
                listOf(
                    databaseModule,
                    repositoryModule,
                    viewModelModule,
                    factoryModule,
                    serializationModule,
                    storageModule,
                    managerModule,
                    useCaseModule
                )
            )
        }
    }

    private val useCaseModule = module {
        factory { AddNewTripUseCase(get()) }
        factory { AddTripTrackUseCase(get()) }

        factory { GetCurrentTripUseCase(get()) }

        factory { UpdateTripUseCase(get()) }

        factory { DeleteTripUseCase(get()) }

        factory { GetTripTracksUseCase(get()) }
    }


    private val storageModule = module {
        single { AppPrefsStorage(get()) }
    }

    private val managerModule = module {
        single {
            LocationTrackingManager(get())
        }
        single {
            LiveTrackingManager.Builder(get())
                .setBackgroundService(TripBackgroundService::class.java)
                .build()
        }
    }

    private val viewModelModule = module {
        viewModel { MapViewModel(get()) }
        viewModel { TripTrackViewModel(get(), get()) }
        viewModel { TripViewModel(get(), get(), get()) }
        viewModel { SettingsViewModel(get(), get()) }
        viewModel { LiveTrackingViewModel(get(), get()) }
        viewModel { TripManageViewModel(get(), get(), get(), get()) }
    }

    private val repositoryModule = module {
        single { TripTrackRepository(get()) }
        single<TripPagingRepository> { TripPagingRepositoryImpl(get()) }
        single { TripRepository(get()) }
    }

    private val databaseModule = module {
        single { LiveTrackingDatabase.getDatabase(get()).tripTrackDao() }
        single { LiveTrackingDatabase.getDatabase(get()).tripDao() }
    }

    private val factoryModule = module {
        single { TripUseCaseFactory(get(), get()) }
    }

    private val serializationModule = module {
        single { Gson() }
    }


}