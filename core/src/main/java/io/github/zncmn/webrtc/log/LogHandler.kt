package io.github.zncmn.webrtc.log

interface LogHandler {
    fun log(priority: Int, tag: String?, t: Throwable?, message: String?, vararg args: Any?) { }
}
