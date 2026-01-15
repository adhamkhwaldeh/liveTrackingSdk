package com.kerberos.trackingSdk.repositories.paging.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kerberos.trackingSdk.mappers.toTripModel
import com.kerberos.trackingSdk.models.TripModel
import com.kerberos.trackingSdk.orm.TripDao

class TripPagingDataSource(
    private val tripDao: TripDao,
) : PagingSource<Int, TripModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TripModel> {
        return try {
            val result = tripDao.getTripsPaging().load(params)

            when (result) {
                is LoadResult.Page -> {
                    val data = result.data.map { trip ->
                        trip.toTripModel()
                    }
                    LoadResult.Page(
                        data = data,
                        prevKey = result.prevKey,
                        nextKey = result.nextKey
                    )
                }

                is LoadResult.Error -> LoadResult.Error(result.throwable)
                is LoadResult.Invalid -> LoadResult.Invalid()
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TripModel>): Int? {
        return state.anchorPosition
    }

//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TripModel> {
//        return try {
//            // Delegate loading to the DAO's PagingSource and map entities to TripModel
//            val source = tripDao.getTripsPaging()
//            val result = source.load(params)
//
//            return when (result) {
//                is LoadResult.Page -> {
//                    val data = result.data.map { trip ->
//                        TripModel(
//                            id = trip.id,
//                            tripDuration = trip.tripDuration,
//                            startTime = trip.startTime,
//                            totalDistance = trip.totalDistance,
//                            endTime = trip.endTime,
//                            isActive = trip.isActive
//                        )
//                    }
//                    // Propagate prevKey / nextKey from the underlying result
//                    LoadResult.Page(
//                        data = data,
//                        prevKey = result.prevKey,
//                        nextKey = result.nextKey
//                    )
//                }
//
//                is LoadResult.Error -> LoadResult.Error(result.throwable)
//                is LoadResult.Invalid -> LoadResult.Invalid()
//            }
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, com.kerberos.trackingSdk.models.TripModel>): Int? {
//        // Standard anchor-based refresh key calculation
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        } ?: PagingParamConfig.initialOffset
//    }
//
//    override val keyReuseSupported: Boolean
//        get() = true

}