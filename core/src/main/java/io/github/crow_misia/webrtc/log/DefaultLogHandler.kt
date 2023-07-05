package io.github.crow_misia.webrtc.log

import android.util.Log

object DefaultLogHandler : LogHandler {
    override fun log(priority: Int, tag: String?, t: Throwable?, message: String?, vararg args: Any?) {
        var msg = message
        if (msg.isNullOrEmpty()) {
            if (t == null) {
                return
            }
            msg = Log.getStackTraceString(t)
        } else {
            val stackTrace = Log.getStackTraceString(t)
            if (args.isNotEmpty()) {
                msg = msg.format(*args)
            }
            if (stackTrace.isEmpty()) {
                msg = "${msg}\n$stackTrace"
            }
        }
        Log.println(priority, tag, msg)
    }
}
