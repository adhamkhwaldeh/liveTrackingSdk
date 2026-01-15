package com.kerberos.trackingSdk.importer

import com.google.gson.Gson
import com.kerberos.trackingSdk.models.TripModel
import java.io.InputStream
import java.io.OutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class JsonTripImporterExporter(private val gson: Gson) : TripImporter, TripExporter {

    override suspend fun import(inputStream: InputStream): List<TripModel> {
        val reader = InputStreamReader(inputStream)
        return gson.fromJson(reader, Array<TripModel>::class.java).toList()
    }

    override suspend fun export(trips: List<TripModel>, outputStream: OutputStream) {
        val writer = OutputStreamWriter(outputStream)
        gson.toJson(trips, writer)
        writer.close()
    }
}
