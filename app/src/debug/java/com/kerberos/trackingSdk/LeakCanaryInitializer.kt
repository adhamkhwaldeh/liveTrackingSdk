package com.kerberos.trackingSdk

import android.app.Application
import leakcanary.LeakCanary

object LeakCanaryInitializer {
    fun initialize(app: Application) {
        if (LeakCanary.isInAnalyzerProcess(app)) return

        LeakCanary.config = LeakCanary.config.copy(
            dumpHeap = true,
            retainedVisibleThreshold = 1,
        )
    }
}
