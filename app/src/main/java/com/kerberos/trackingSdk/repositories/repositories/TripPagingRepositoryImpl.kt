package com.kerberos.trackingSdk.repositories.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.orm.TripDao
import com.kerberos.trackingSdk.repositories.paging.PagingParamConfig
import com.kerberos.trackingSdk.repositories.paging.dataSource.TripPagingDataSource
import kotlinx.coroutines.flow.Flow

class TripPagingRepositoryImpl(private val tripDao: TripDao) :
    TripPagingRepository {


    @ExperimentalPagingApi
    override fun getTripPageList(): Flow<PagingData<TripModel>> =
        Pager(
            config = PagingConfig(
                pageSize = PagingParamConfig.pageSize,
//                prefetchDistance = PagingParamConfig.prefetchDistance,
                enablePlaceholders = PagingParamConfig.enablePlaceholders,
//                initialLoadSize = PagingParamConfig.initialLoadSize,
            ),
            pagingSourceFactory = { TripPagingDataSource(tripDao) },
//            initialKey = payload,
//            remoteMediator = null,
        ).flow

}