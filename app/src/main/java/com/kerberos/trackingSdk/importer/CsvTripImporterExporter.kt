package com.kerberos.trackingSdk.importer

import com.kerberos.trackingSdk.models.TripModel
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import java.io.InputStream
import java.io.OutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class CsvTripImporterExporter : TripImporter, TripExporter {

    override suspend fun import(inputStream: InputStream): List<TripModel> {
        val reader = CSVReader(InputStreamReader(inputStream))
        val trips = mutableListOf<TripModel>()
        var nextLine: Array<String>?
        reader.readNext() // skip header
        while (reader.readNext().also { nextLine = it } != null) {
            nextLine?.let {
                trips.add(
                    TripModel(
                        id = it[0].toInt(),
                        startTime = it[1].toLong(),
                        endTime = it[2].toLongOrNull(),
                        tripDuration = it[3].toLong(),
                        totalDistance = it[4].toDouble(),
                        isActive = it[5].toBoolean()
                    )
                )
            }
        }
        return trips
    }

    override suspend fun export(trips: List<TripModel>, outputStream: OutputStream) {
        val writer = CSVWriter(OutputStreamWriter(outputStream))
        writer.writeNext(
            arrayOf(
                "id",
                "startTime",
                "endTime",
                "tripDuration",
                "totalDistance",
                "isActive"
            )
        )
        trips.forEach {
            writer.writeNext(
                arrayOf(
                    it.id.toString(),
                    it.startTime.toString(),
                    it.endTime?.toString(),
                    it.tripDuration?.toString(),
                    it.totalDistance?.toString(),
                    it.isActive.toString()
                )
            )
        }
        writer.close()
    }
}
