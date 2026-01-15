package com.kerberos.trackingSdk.importer

import com.kerberos.trackingSdk.models.TripModel
import java.io.InputStream

interface TripImporter {
    suspend fun import(inputStream: InputStream): List<TripModel>
}