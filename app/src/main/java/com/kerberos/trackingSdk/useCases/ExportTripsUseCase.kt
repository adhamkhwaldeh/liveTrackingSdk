package com.kerberos.trackingSdk.useCases

import com.kerberos.trackingSdk.importer.TripExporter
import com.kerberos.trackingSdk.mappers.toTripModel
import com.kerberos.trackingSdk.repositories.repositories.TripRepository
import java.io.OutputStream

class ExportTripsUseCase(
    private val tripRepository: TripRepository,
    private val tripExporter:TripExporter
) {
    suspend fun execute(outputStream: OutputStream) {
        val trips = tripRepository.getAllTrips().map { it.toTripModel() }
        tripExporter.export(trips, outputStream)
    }
}
