@file:Suppress("NOTHING_TO_INLINE", "unused")

package io.github.zncmn.webrtc.observer

import io.github.zncmn.webrtc.log.WebRtcLogger
import org.webrtc.*

inline fun PeerConnection.Observer.wrapLog(tag: String): PeerConnection.Observer {
    return LogPeerConnectionObserver(tag, this)
}

class LogPeerConnectionObserver(
    private val tag: String,
    private val observer: PeerConnection.Observer
) : PeerConnection.Observer {
    override fun onSignalingChange(newState: PeerConnection.SignalingState) {
        WebRtcLogger.d(tag, "onSignalingChange [newState:%s]", newState)
        observer.onSignalingChange(newState)
    }

    override fun onAddStream(stream: MediaStream) {
        WebRtcLogger.d(tag, "onAddStream [%s]", stream.id)
        observer.onAddStream(stream)
    }

    override fun onRemoveStream(stream: MediaStream) {
        WebRtcLogger.d(tag, "onRemoveStream [%s]", stream.id)
        observer.onRemoveStream(stream)
    }

    override fun onDataChannel(dataChannel: DataChannel) {
        WebRtcLogger.d(tag, "onDataChannel [%s,%s]", dataChannel.id(), dataChannel.label())
        observer.onDataChannel(dataChannel)
    }

    override fun onRenegotiationNeeded() {
        WebRtcLogger.d(tag, "onRenegotiationNeeded")
        observer.onRenegotiationNeeded()
    }

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {
        WebRtcLogger.d(tag, "onIceConnectionChange [newState:%s]", newState)
        observer.onIceConnectionChange(newState)
    }

    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) {
        WebRtcLogger.d(tag, "onIceGatheringChange [newState:%s]", newState)
        observer.onIceGatheringChange(newState)
    }

    override fun onIceCandidate(candidate: IceCandidate) {
        WebRtcLogger.d(tag, "onIceCandidate [candidate:%s]", candidate.toString())
        observer.onIceCandidate(candidate)
    }

    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {
        WebRtcLogger.d(tag, "onIceCandidatesRemoved [candidatess:%d]", candidates.size)
        observer.onIceCandidatesRemoved(candidates)
    }

    override fun onIceConnectionReceivingChange(receiving: Boolean) {
        WebRtcLogger.d(tag, "onIceConnectionReceivingChange [receiving:%b]", receiving)
        observer.onIceConnectionReceivingChange(receiving)
    }

    override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<out MediaStream>) {
        WebRtcLogger.d(tag, "onAddTrack [receiver:%s, streams:%d]", receiver.id(), mediaStreams.size)
        observer.onAddTrack(receiver, mediaStreams)
    }

    override fun onTrack(transceiver: RtpTransceiver) {
        WebRtcLogger.d(tag, "onTrack [transceiver:%s]", transceiver.mid)
        observer.onTrack(transceiver)
    }

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState) {
        WebRtcLogger.d(tag, "onConnectionChange [newState:%s]", newState)
        observer.onConnectionChange(newState)
    }
}