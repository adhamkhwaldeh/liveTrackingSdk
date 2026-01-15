package com.kerberos.trackingSdk.importer

import com.kerberos.trackingSdk.models.TripModel
import java.io.OutputStream

interface TripExporter {
    suspend fun export(trips: List<TripModel>, outputStream: OutputStream)
}
