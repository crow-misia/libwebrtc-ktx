package io.github.zncmn.webrtc.observer

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class BaseSdpObserver<T>(
    protected val continuation: Continuation<T>
) : SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription) = Unit
    override fun onCreateFailure(error: String) {
        continuation.resumeWithException(SdpObserverException(error))
    }
    override fun onSetSuccess() = Unit
    override fun onSetFailure(error: String) {
        continuation.resumeWithException(SdpObserverException(error))
    }
}

class CreateSdpObserver(
    continuation: Continuation<SessionDescription>
) : BaseSdpObserver<SessionDescription>(continuation) {
    override fun onCreateSuccess(sdp: SessionDescription) {
        continuation.resume(sdp)
    }
}

class SetSdpObserver(
    private val sdp: SessionDescription,
    continuation: Continuation<SessionDescription>
) : BaseSdpObserver<SessionDescription>(continuation) {
    override fun onSetSuccess() {
        continuation.resume(sdp)
    }
}

class SdpObserverException(error: String): Exception(error)
