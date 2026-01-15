package com.kerberos.livetrackingsdk.exceptions

class PermissionNotGrantedException(
    override val message: String? = "Location permission was revoked or not granted at time of request."
) : SecurityException()