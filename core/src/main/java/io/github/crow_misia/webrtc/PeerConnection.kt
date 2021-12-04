@file:Suppress("unused")

package io.github.crow_misia.webrtc

import io.github.crow_misia.webrtc.observer.CreateSdpObserver
import io.github.crow_misia.webrtc.observer.SetSdpObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.RTCStatsReport
import org.webrtc.SessionDescription
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend inline fun PeerConnection.setLocalDescription() {
    return suspendCoroutine {
        val observer = SetSdpObserver(it)
        setLocalDescription(observer)
    }
}

suspend inline fun PeerConnection.setLocalDescription(desc: SessionDescription) {
    return suspendCoroutine {
        val observer = SetSdpObserver(it)
        setLocalDescription(observer, desc)
    }
}

suspend inline fun PeerConnection.setRemoteDescription(desc: SessionDescription) {
    return suspendCoroutine {
        val observer = SetSdpObserver(it)
        setRemoteDescription(observer, desc)
    }
}

suspend inline fun PeerConnection.createOffer(constraints: MediaConstraints): SessionDescription {
    return suspendCoroutine {
        val observer = CreateSdpObserver(it)
        createOffer(observer, constraints)
    }
}

suspend inline fun PeerConnection.createAnswer(constraints: MediaConstraints): SessionDescription {
    return suspendCoroutine {
        val observer = CreateSdpObserver(it)
        createAnswer(observer, constraints)
    }
}

@ExperimentalCoroutinesApi
suspend inline fun PeerConnection.getStats(): RTCStatsReport {
    return suspendCoroutine {
        getStats { report -> it.resume(report) }
    }
}
