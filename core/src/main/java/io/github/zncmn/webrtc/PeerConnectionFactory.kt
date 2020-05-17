@file:Suppress("NOTHING_TO_INLINE", "unused")

package io.github.zncmn.webrtc

import android.app.Application
import org.webrtc.Logging
import org.webrtc.PeerConnectionFactory

inline fun Application.initializePeerConnectionFactory(useTracer: Boolean = false,
                                                       fieldTrials: String? = null,
                                                       libwebrtcLoggingEnable: Boolean = false) {
    val options = PeerConnectionFactory.InitializationOptions
        .builder(this)
        .setFieldTrials(fieldTrials)
        .setEnableInternalTracer(useTracer)
        .createInitializationOptions()
    PeerConnectionFactory.initialize(options)
    if (libwebrtcLoggingEnable) {
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)
    }
}