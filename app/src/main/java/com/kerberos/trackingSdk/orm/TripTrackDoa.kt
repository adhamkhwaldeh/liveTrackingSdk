package com.kerberos.trackingSdk.orm

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kerberos.trackingSdk.helpers.LocationDistanceHelper

@Dao
interface TripTrackDoa : BaseDao<TripTrack> {

    @Query("select * from triptrack")
    fun loadAllData(): List<TripTrack>

    @Query("SELECT * FROM triptrack ORDER BY id DESC")
    fun getTripsPaging(): PagingSource<Int, TripTrack>

    @Query("SELECT COUNT(*) FROM triptrack where tripId=:tripId")
    fun tracksInTrip(tripId: Int): Int

    @Query("select * from triptrack where tripId=:tripId ")
    fun getTripTracks(tripId: Int): List<TripTrack>

    @Query("select * from triptrack where tripId=:tripId ORDER BY id DESC LIMIT 1")
    fun getLatestTripTracks(tripId: Int): TripTrack?

    @Query("SELECT * FROM trip WHERE id = :tripId")
    fun getTripById(tripId: Int): Trip?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateTrip(trip: Trip)

    // Transaction method
    @Transaction
    suspend fun addPointAndUpdateTrip(
        newPoint: TripTrack,
    ) {
        val currentTrip = getTripById(newPoint.tripId) ?: return

        val latestPoint = getLatestTripTracks(newPoint.tripId)
        if (latestPoint != null) {
            currentTrip.totalDistance += LocationDistanceHelper.distanceMeter(
                latestPoint.latitude,
                latestPoint.longitude,
                newPoint.latitude,
                newPoint.longitude
            )
            currentTrip.tripDuration += newPoint.timestamp - latestPoint.timestamp
        }

        currentTrip.totalPoints += 1

        insert(newPoint)

        updateTrip(currentTrip)

    }
}