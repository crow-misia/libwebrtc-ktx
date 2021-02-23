@file:Suppress("unused")

package io.github.crow_misia.webrtc

import io.github.crow_misia.webrtc.observer.CreateSdpObserver
import io.github.crow_misia.webrtc.observer.SetSdpObserver
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import kotlin.coroutines.suspendCoroutine

suspend inline fun PeerConnection.setLocalDescription(sdp: SessionDescription): SessionDescription {
    return suspendCoroutine {
        val observer = SetSdpObserver(sdp, it)
        setLocalDescription(observer, sdp)
    }
}

suspend inline fun PeerConnection.setRemoteDescription(sdp: SessionDescription): SessionDescription {
    return suspendCoroutine {
        val observer = SetSdpObserver(sdp, it)
        setRemoteDescription(observer, sdp)
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
