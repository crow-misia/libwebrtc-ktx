@file:Suppress("unused")

package io.github.crow_misia.webrtc.log

import android.util.Log

object WebRtcLogger {
    private var handler: LogHandler = DefaultLogHandler

    @JvmStatic
    fun setHandler(handler: LogHandler) {
        this.handler = handler
    }

    @JvmStatic
    fun v(tag: String?, message: String?, vararg args: Any?) {
        handler.log(Log.VERBOSE, tag, null, message, *args)
    }

    @JvmStatic
    fun v(tag: String?, t: Throwable?, message: String, vararg args: Any?) {
        handler.log(Log.VERBOSE, tag, t, message, *args)
    }

    @JvmStatic
    fun v(tag: String?, t: Throwable?) {
        handler.log(Log.VERBOSE, tag, t, null)
    }

    @JvmStatic
    fun d(tag: String?, message: String?, vararg args: Any?) {
        handler.log(Log.DEBUG, tag, null, message, *args)
    }

    @JvmStatic
    fun d(tag: String?, t: Throwable?, message: String?, vararg args: Any?) {
        handler.log(Log.DEBUG, tag, t, message, *args)
    }

    @JvmStatic
    fun d(tag: String?, t: Throwable?) {
        handler.log(Log.DEBUG, tag, t, null)
    }

    @JvmStatic
    fun i(tag: String?, message: String?, vararg args: Any?) {
        handler.log(Log.INFO, tag, null, message, *args)
    }

    @JvmStatic
    fun i(tag: String?, t: Throwable?, message: String?, vararg args: Any?) {
        handler.log(Log.INFO, tag, t, message, *args)
    }

    @JvmStatic
    fun i(tag: String?, t: Throwable?) {
        handler.log(Log.INFO, tag, t, null)
    }

    @JvmStatic
    fun w(tag: String?, message: String?, vararg args: Any?) {
        handler.log(Log.WARN, tag, null, message, *args)
    }

    @JvmStatic
    fun w(tag: String?, t: Throwable?, message: String?, vararg args: Any?) {
        handler.log(Log.WARN, tag, t, message, *args)
    }

    @JvmStatic
    fun w(tag: String?, t: Throwable?) {
        handler.log(Log.WARN, tag, t, null)
    }

    @JvmStatic
    fun e(tag: String?, message: String?, vararg args: Any?) {
        handler.log(Log.ERROR, tag, null, message, *args)
    }

    @JvmStatic
    fun e(tag: String?, t: Throwable?, message: String?, vararg args: Any?) {
        handler.log(Log.ERROR, tag, t, message, *args)
    }

    @JvmStatic
    fun e(tag: String?, t: Throwable?) {
        handler.log(Log.ERROR, tag, t, null)
    }

    @JvmStatic
    fun println(priority: Int, tag: String?, message: String?) {
        handler.log(priority, tag, null, message)
    }
}
