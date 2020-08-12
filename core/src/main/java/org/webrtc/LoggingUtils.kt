package org.webrtc

import android.util.Log

internal object LoggingUtils {
    fun injectLoggable(injectedLoggable: Loggable, severity: Logging.Severity) {
        Logging.injectLoggable(injectedLoggable, severity)
    }
}

internal fun Logging.Severity.toLogLevel(): Int? {
    return when (this) {
        Logging.Severity.LS_VERBOSE -> Log.VERBOSE
        Logging.Severity.LS_INFO -> Log.INFO
        Logging.Severity.LS_WARNING -> Log.WARN
        Logging.Severity.LS_ERROR -> Log.ERROR
        else -> null
    }
}