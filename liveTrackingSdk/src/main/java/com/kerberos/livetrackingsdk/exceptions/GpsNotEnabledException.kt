package com.kerberos.livetrackingsdk.exceptions

class GpsNotEnabledException(override val message: String? = "GPS is not enabled. Please enable GPS to proceed.") :
    Exception()