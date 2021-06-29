@file:Suppress("NOTHING_TO_INLINE", "unused")

package io.github.crow_misia.webrtc

import android.app.Application
import android.util.Log
import io.github.crow_misia.webrtc.log.WebRtcLogger
import org.webrtc.Loggable
import org.webrtc.Logging
import org.webrtc.NativeLibraryLoader
import org.webrtc.PeerConnectionFactory

@JvmOverloads
fun Application.initializePeerConnectionFactory(
    useTracer: Boolean = false,
    fieldTrials: String? = null,
    loggableSeverity: Logging.Severity = Logging.Severity.LS_NONE,
    nativeLibraryName: String? = null,
    nativeLibraryLoader: NativeLibraryLoader? = null,
) {
    val options = PeerConnectionFactory.InitializationOptions.builder(this).also { builder ->
        builder.setEnableInternalTracer(useTracer)
        builder.setInjectableLogger(generateInjectableLogger(loggableSeverity), loggableSeverity)
        fieldTrials?.also { builder.setFieldTrials(it) }
        nativeLibraryName?.also { builder.setNativeLibraryName(it) }
        nativeLibraryLoader?.also { builder.setNativeLibraryLoader(it) }
    }.createInitializationOptions()

    PeerConnectionFactory.initialize(options)
}

private fun generateInjectableLogger(loggableSeverity: Logging.Severity): Loggable? {
    return if (loggableSeverity == Logging.Severity.LS_NONE) {
        null
    } else {
        Loggable { message, severity, tag ->
            severity.toLogLevel()?.also { WebRtcLogger.println(it, tag, message) }
        }
    }
}

private fun Logging.Severity.toLogLevel(): Int? {
    return when (this) {
        Logging.Severity.LS_VERBOSE -> Log.VERBOSE
        Logging.Severity.LS_INFO -> Log.INFO
        Logging.Severity.LS_WARNING -> Log.WARN
        Logging.Severity.LS_ERROR -> Log.ERROR
        else -> null
    }
}
