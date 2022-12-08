@file:Suppress("unused")

package io.github.crow_misia.webrtc.observer

import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection

interface PeerConnectionDefaultObserver : PeerConnection.Observer {
    override fun onSignalingChange(newState: PeerConnection.SignalingState) = Unit

    override fun onAddStream(stream: MediaStream) = Unit

    override fun onRemoveStream(stream: MediaStream) = Unit

    override fun onDataChannel(dataChannel: DataChannel) = Unit

    override fun onRenegotiationNeeded() = Unit

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) = Unit

    override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) = Unit

    override fun onIceCandidate(candidate: IceCandidate) = Unit

    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) = Unit

    override fun onIceConnectionReceivingChange(receiving: Boolean) = Unit
}