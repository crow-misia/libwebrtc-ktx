@file:Suppress("unused")

package io.github.crow_misia.webrtc.observer

import org.webrtc.*

interface PeerConnectionDefaultObserver : PeerConnection.Observer {
    override fun onSignalingChange(newState: PeerConnection.SignalingState) { }

    override fun onAddStream(stream: MediaStream) { }

    override fun onRemoveStream(stream: MediaStream) { }

    override fun onDataChannel(dataChannel: DataChannel) { }

    override fun onRenegotiationNeeded() { }

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) { }

    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) { }

    override fun onIceCandidate(candidate: IceCandidate) { }

    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) { }

    override fun onIceConnectionReceivingChange(receiving: Boolean) { }

    override fun onAddTrack(receiver: RtpReceiver, mediaStreams: Array<out MediaStream>) { }
}