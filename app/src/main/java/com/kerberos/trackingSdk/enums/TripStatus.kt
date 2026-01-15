package com.kerberos.trackingSdk.enums

enum class TripStatus {
    STOPPED,
    RUNNING,
    PAUSED;

    companion object {
        fun isStoppable(status: TripStatus): Boolean {
            return status == RUNNING || status == PAUSED
        }
    }
}