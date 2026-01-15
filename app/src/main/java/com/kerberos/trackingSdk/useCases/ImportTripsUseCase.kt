package com.kerberos.trackingSdk.useCases

import com.kerberos.trackingSdk.importer.TripImporter
import com.kerberos.trackingSdk.mappers.toTrip
import com.kerberos.trackingSdk.repositories.repositories.TripRepository
import java.io.InputStream

class ImportTripsUseCase(
    private val tripRepository: TripRepository,
    private val tripImporter: TripImporter
) {
    suspend fun execute(inputStream: InputStream) {
        val trips = tripImporter.import(inputStream).map { it }
        tripRepository.insertTrips(trips)
    }
}
