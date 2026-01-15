package com.kerberos.trackingSdk.orm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Trip::class, TripTrack::class], version = 5)
abstract class LiveTrackingDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    abstract fun tripTrackDao(): TripTrackDoa

    companion object {
        @Volatile
        private var INSTANCE: LiveTrackingDatabase? = null

        fun getDatabase(context: Context): LiveTrackingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LiveTrackingDatabase::class.java,
                    "live_tracking_database"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

