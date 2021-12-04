package io.github.crow_misia.webrtc.observer

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class BaseSdpObserver<T>(
    protected val continuation: Continuation<T>,
) : SdpObserver {
    override fun onCreateFailure(error: String) {
        continuation.resumeWithException(SdpObserverException(error))
    }
    override fun onSetFailure(error: String) {
        continuation.resumeWithException(SdpObserverException(error))
    }
}

class CreateSdpObserver(
    continuation: Continuation<SessionDescription>,
) : BaseSdpObserver<SessionDescription>(continuation) {
    override fun onSetSuccess() {
        continuation.resumeWithException(SdpObserverException("illegal operation."))
    }
    override fun onCreateSuccess(desc: SessionDescription) {
        continuation.resume(desc)
    }
}

class SetSdpObserver(
    continuation: Continuation<Unit>,
) : BaseSdpObserver<Unit>(continuation) {
    override fun onSetSuccess() {
        continuation.resume(Unit)
    }
    override fun onCreateSuccess(desc: SessionDescription) {
        continuation.resumeWithException(SdpObserverException("illegal operation."))
    }
}

class SdpObserverException(error: String): Exception(error)
