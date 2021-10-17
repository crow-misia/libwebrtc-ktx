@file:Suppress("unused")

package io.github.crow_misia.webrtc

import io.github.crow_misia.webrtc.log.WebRtcLogger
import io.github.crow_misia.webrtc.option.MediaConstraintsOption
import org.webrtc.*
import java.util.*

sealed interface RTCLocalAudioManager {
    val track: MediaStreamTrack?
    var enabled: Boolean

    fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption)
    fun dispose()
}

class RTCNoneLocalAudioManager : RTCLocalAudioManager {
    override val track: MediaStreamTrack? = null
    override var enabled: Boolean = false
        set(_) { field = false }

    override fun initTrack(factory: PeerConnectionFactory, option: MediaConstraintsOption) = Unit
    override fun dispose() = Unit
}

class RTCLocalAudioManagerImpl : RTCLocalAudioManager {
    companion object {
        private val TAG = RTCLocalAudioManagerImpl::class.simpleName
    }

    private var source: AudioSource? = null

    override var track:  AudioTrack?  = null
        private set

    override var enabled
        get() =  track?.enabled() ?: false
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
        return MediaConstraints().apply {
            // echo cancellation
            addMandatory(MediaConstraintsOption.ECHO_CANCELLATION_CONSTRAINT, option.audioProcessingEchoCancellation)
            // auto gain control
            addMandatory(MediaConstraintsOption.AUTO_GAIN_CONTROL_CONSTRAINT, option.audioProcessingAutoGainControl)
            // experimental auto gain control
            addMandatory(MediaConstraintsOption.EXPERIMENTAL_AUTO_GAIN_CONTROL_CONSTRAINT, option.audioProcessingExperimentalAGC)
            // highpass filter
            addMandatory(MediaConstraintsOption.HIGH_PASS_FILTER_CONSTRAINT, option.audioProcessingHighpassFilter)
            // noise suppression
            addMandatory(MediaConstraintsOption.NOISE_SUPPRESSION_CONSTRAINT, option.audioProcessingNoiseSuppression)
            // experimental noise suppression
            addMandatory(MediaConstraintsOption.EXPERIMENTAL_NOISE_SUPPRESSION_CONSTRAINT, option.audioProcessingExperimentalNS)
            // typing noise detection
            addMandatory(MediaConstraintsOption.TYPING_NOISE_DETECTION_CONSTRAINT, option.audioProcessingTypingNoiseDetection)
            // audio mirroring
            addMandatory(MediaConstraintsOption.AUDIO_MIRRORING_CONSTRAINT, option.audioProcessingAudioMirroring)
        }
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

    private fun MediaConstraints.addMandatory(name: String, value: Boolean) {
        val valueString = if (value) "true" else "false"
        mandatory.add(MediaConstraints.KeyValuePair(name, valueString))
    }
}
