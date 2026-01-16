package com.kerberos.livetrackingsdk.configs

import com.github.adhamkhwaldeh.commonsdk.listeners.configs.ManagerConfigInterface
import com.github.adhamkhwaldeh.commonsdk.logging.LogLevel

class TrackingConfig(
    override var isEnabled: Boolean,
    override var isDebugMode: Boolean,
    override var isLoggingEnabled: Boolean,
    override var overridable: Boolean,
    override var logLevel: LogLevel
) : ManagerConfigInterface
