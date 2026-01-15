package com.kerberos.trackingSdk.orm

import androidx.room.Dao
import androidx.room.Query
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao : BaseDao<Trip> {
    @Query("select * from trip")
    fun loadAllData(): List<Trip>

    @Query("SELECT * FROM trip ORDER BY id DESC")
    fun getTripsPaging(): PagingSource<Int, Trip>

    @Query("SELECT * FROM trip WHERE isActive = 1 || endTime IS NULL ORDER BY id DESC limit 1")
    fun getActiveTrip(): Flow<Trip?>

    @Query("SELECT * FROM trip WHERE id = :tripId")
    fun getTripById(tripId: Int): Trip?


}