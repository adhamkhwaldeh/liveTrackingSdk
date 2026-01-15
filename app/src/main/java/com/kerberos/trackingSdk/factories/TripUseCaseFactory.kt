package com.kerberos.trackingSdk.factories

import com.google.gson.Gson
import com.kerberos.trackingSdk.importer.CsvTripImporterExporter
import com.kerberos.trackingSdk.importer.JsonTripImporterExporter
import com.kerberos.trackingSdk.repositories.repositories.TripRepository
import com.kerberos.trackingSdk.useCases.ExportTripsUseCase
import com.kerberos.trackingSdk.useCases.ImportTripsUseCase

class TripUseCaseFactory(private val tripRepository: TripRepository, private val gson: Gson) {

    fun createImportUseCase(type: String): ImportTripsUseCase {
        val importer = when (type) {
            "csv" -> CsvTripImporterExporter()
            "json" -> JsonTripImporterExporter(gson)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
        return ImportTripsUseCase(tripRepository, importer)
    }

    fun createExportUseCase(type: String): ExportTripsUseCase {
        val exporter = when (type) {
            "csv" -> CsvTripImporterExporter()
            "json" -> JsonTripImporterExporter(gson)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
        return ExportTripsUseCase(tripRepository, exporter)
    }

}
