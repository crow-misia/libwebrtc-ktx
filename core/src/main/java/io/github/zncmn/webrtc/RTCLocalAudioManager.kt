@file:Suppress("unused")

package io.github.zncmn.webrtc

import io.github.zncmn.webrtc.log.WebRtcLogger
import io.github.zncmn.webrtc.option.MediaConstraintsOption
import org.webrtc.*
import java.util.*

interface RTCLocalAudioManager {
    val track: MediaStreamTrack?
    var enabled: Boolean

    fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption)
    fun dispose()
}

class RTCNoneLocalAudioManager : RTCLocalAudioManager {
    override val track: MediaStreamTrack? = null
    override var enabled: Boolean = false

    override fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption) = Unit
    override fun dispose() = Unit
}

class RTCLocalAudioManagerImpl : RTCLocalAudioManager {
    companion object {
        private val TAG = RTCLocalAudioManagerImpl::class.simpleName
    }

    private var source: AudioSource? = null
    override var track:  AudioTrack?  = null

    override var enabled: Boolean
        get() = track?.enabled() ?: false
        set(value) { track?.setEnabled(value) }

    override fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption) {
        WebRtcLogger.d(TAG, "initTrack")

        val constraints = createSourceConstraints(option)
        source = factory.createAudioSource(constraints)
        WebRtcLogger.d(TAG, "audio source created: %s", source)
        val trackId = UUID.randomUUID().toString()
        track = factory.createAudioTrack(trackId, source)
        track?.setEnabled(true)

        WebRtcLogger.d(TAG, "audio track created: %s", track)
    }

    private fun createSourceConstraints(option: MediaConstraintsOption): MediaConstraints {
        val constraints = MediaConstraints()
        if (!option.audioProcessingEchoCancellation) {
            constraints.mandatory.add(
                MediaConstraints.KeyValuePair(MediaConstraintsOption.ECHO_CANCELLATION_CONSTRAINT, "false"))
        }
        if(!option.audioProcessingAutoGainControl) {
            constraints.mandatory.add(
                MediaConstraints.KeyValuePair(MediaConstraintsOption.AUTO_GAIN_CONTROL_CONSTRAINT, "false"))
        }
        if (!option.audioProcessingHighpassFilter) {
            constraints.mandatory.add(
                MediaConstraints.KeyValuePair(MediaConstraintsOption.HIGH_PASS_FILTER_CONSTRAINT, "false"))
        }
        if (!option.audioProcessingNoiseSuppression) {
            constraints.mandatory.add(
                MediaConstraints.KeyValuePair(MediaConstraintsOption.NOISE_SUPPRESSION_CONSTRAINT, "false"))
        }
        return constraints
    }

    override fun dispose() {
        WebRtcLogger.d(TAG, "dispose")

        WebRtcLogger.d(TAG, "dispose track")
        track?.also {
            it.setEnabled(false)
            it.dispose()
        }
        track = null

        WebRtcLogger.d(TAG, "dispose source")
        source?.dispose()
        source = null
    }
}
