package com.kerberos.livetrackingsdk.configs

import com.github.adhamkhwaldeh.commonsdk.logging.LogLevel
import com.github.adhamkhwaldeh.commonsdk.options.BaseSDKOptions

class LiveTrackingConfig(
    override var isEnabled: Boolean,
    override var isDebugMode: Boolean,
    override var isLoggingEnabled: Boolean,
    override var logLevel: LogLevel,
    override var overridable: Boolean
) : BaseSDKOptions()