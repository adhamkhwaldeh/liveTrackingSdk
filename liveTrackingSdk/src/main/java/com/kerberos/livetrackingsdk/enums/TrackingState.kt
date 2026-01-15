package com.kerberos.livetrackingsdk.enums

enum class TrackingState {
    IDLE, STARTED, PAUSED, STOPPED;

    companion object {
        fun startable(state: TrackingState): Boolean {
            return state == IDLE || state == STOPPED
        }

        fun stoppable(state: TrackingState): Boolean {
            return state == STARTED || state == PAUSED
        }
    }
}