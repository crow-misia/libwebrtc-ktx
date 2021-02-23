@file:Suppress("NOTHING_TO_INLINE", "unused")

package io.github.crow_misia.webrtc

import android.app.Application
import io.github.crow_misia.webrtc.log.WebRtcLogger
import org.webrtc.*
import org.webrtc.LoggingUtils

fun Application.initializePeerConnectionFactory(useTracer: Boolean = false,
                                                fieldTrials: String? = null,
                                                libwebrtcLoggingSeverity: Logging.Severity = Logging.Severity.LS_NONE,
                                                nativeLibraryName: String = "jingle_peerconnection_so") {
    val options = PeerConnectionFactory.InitializationOptions
        .builder(this)
        .setFieldTrials(fieldTrials)
        .setEnableInternalTracer(useTracer)
        .setNativeLibraryName(nativeLibraryName)
        .createInitializationOptions()
    PeerConnectionFactory.initialize(options)

    LoggingUtils.injectLoggable(Loggable { message, severity, tag ->
        severity.toLogLevel()?.also { WebRtcLogger.println(it, tag, message) }
    }, libwebrtcLoggingSeverity)
}
