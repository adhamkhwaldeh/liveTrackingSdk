package com.kerberos.trackingSdk.helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(date)
}

fun Long.formatDuration(): String {
    val diff = this
    val seconds = diff / 1000 % 60
    val minutes = diff / (1000 * 60) % 60
    val hours = diff / (1000 * 60 * 60)
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
